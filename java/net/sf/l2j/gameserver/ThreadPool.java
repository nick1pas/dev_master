package net.sf.l2j.gameserver;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

import net.sf.l2j.Config;

public final class ThreadPool
{
	protected static final Logger LOG = Logger.getLogger(ThreadPool.class.getName());
	
	private static final int CPU = Runtime.getRuntime().availableProcessors();
	private static final long MAX_DELAY = TimeUnit.DAYS.toMillis(365);
	
	private static int _poolBalancer;
	
	private static ScheduledThreadPoolExecutor[] _scheduledPools;
	private static ThreadPoolExecutor[] _instantPools;
	
	private static volatile boolean SHUTTING_DOWN = false;
	
	// Métricas
	private static final AtomicLong scheduledTasks = new AtomicLong();
	private static final AtomicLong instantTasks = new AtomicLong();
	private static final AtomicLong rejectedTasks = new AtomicLong();
	
	// Controle das tasks periódicas
	private static final Set<ScheduledFuture<?>> FUTURES = Collections.synchronizedSet(new HashSet<>());
	
	/**
	 * Inicializa os pools.
	 */
	public static void init()
	{
		SHUTTING_DOWN = false;
		
		int schedCount = Config.SCHEDULED_THREAD_POOL_COUNT == -1 ? CPU : Config.SCHEDULED_THREAD_POOL_COUNT;
		int instCount = Config.INSTANT_THREAD_POOL_COUNT == -1 ? CPU : Config.INSTANT_THREAD_POOL_COUNT;
		
		_scheduledPools = new ScheduledThreadPoolExecutor[schedCount];
		for (int i = 0; i < schedCount; i++)
			_scheduledPools[i] = new ScheduledThreadPoolExecutor(Config.THREADS_PER_SCHEDULED_THREAD_POOL);
		
		_instantPools = new ThreadPoolExecutor[instCount];
		for (int i = 0; i < instCount; i++)
			_instantPools[i] = new ThreadPoolExecutor(Config.THREADS_PER_INSTANT_THREAD_POOL, Config.THREADS_PER_INSTANT_THREAD_POOL, 5L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(50000), (r, executor) -> {
				rejectedTasks.incrementAndGet();
				LOG.warning("ThreadPool: task descartada por sobrecarga.");
			});
		
		for (ScheduledThreadPoolExecutor pool : _scheduledPools)
			pool.prestartAllCoreThreads();
		
		for (ThreadPoolExecutor pool : _instantPools)
			pool.prestartAllCoreThreads();
		
		// Purge automático
		scheduleAtFixedRate(() -> {
			for (ScheduledThreadPoolExecutor pool : _scheduledPools)
				pool.purge();
			for (ThreadPoolExecutor pool : _instantPools)
				pool.purge();
		}, 300000, 300000);
		
		LOG.info("ThreadPool: iniciado com " + schedCount + " scheduled pools e " + instCount + " instant pools.");
	}
	
	/* ========================= EXECUÇÃO ========================= */
	
	private static boolean canSchedule()
	{
		return !SHUTTING_DOWN;
	}
	
	public static ScheduledFuture<?> schedule(Runnable r, long delay)
	{
		if (!canSchedule())
			return null;
		
		ScheduledFuture<?> future = getScheduledPool().schedule(new SafeTask(r), validate(delay), TimeUnit.MILLISECONDS);
		FUTURES.add(future);
		scheduledTasks.incrementAndGet();
		return future;
	}
	
	public static ScheduledFuture<?> scheduleAtFixedRate(Runnable r, long delay, long period)
	{
		if (!canSchedule())
			return null;
		
		ScheduledFuture<?> future = getScheduledPool().scheduleAtFixedRate(new SafeTask(r), validate(delay), validate(period), TimeUnit.MILLISECONDS);
		FUTURES.add(future);
		scheduledTasks.incrementAndGet();
		return future;
	}
	
	public static void execute(Runnable r)
	{
		if (!canSchedule())
			return;
		
		getInstantPool().execute(new SafeTask(r));
		instantTasks.incrementAndGet();
	}
	
	/* ========================= POOL SELECTION ========================= */
	
	private static ScheduledThreadPoolExecutor getScheduledPool()
	{
		return _scheduledPools[_poolBalancer++ % _scheduledPools.length];
	}
	
	private static ThreadPoolExecutor getInstantPool()
	{
		return _instantPools[_poolBalancer++ % _instantPools.length];
	}
	
	private static long validate(long delay)
	{
		return Math.max(0, Math.min(MAX_DELAY, delay));
	}
	
	/* ========================= SHUTDOWN ========================= */
	
	public static void shutdown()
	{
		try
		{
			SHUTTING_DOWN = true;
			LOG.info("ThreadPool: iniciando desligamento seguro...");
			
			synchronized (FUTURES)
			{
				for (ScheduledFuture<?> future : FUTURES)
					future.cancel(true);
				FUTURES.clear();
			}
			
			for (ScheduledThreadPoolExecutor pool : _scheduledPools)
			{
				pool.shutdown();
				pool.awaitTermination(3, TimeUnit.SECONDS);
				pool.shutdownNow();
			}
			
			for (ThreadPoolExecutor pool : _instantPools)
			{
				pool.shutdown();
				pool.awaitTermination(3, TimeUnit.SECONDS);
				pool.shutdownNow();
			}
			
			LOG.info("ThreadPool: desligado com sucesso.");
		}
		catch (Throwable t)
		{
			LOG.warning("ThreadPool: erro ao desligar - " + t.getMessage());
		}
	}
	
	public static boolean isShuttingDown()
	{
		return SHUTTING_DOWN;
	}
	
	/* ========================= MÉTRICAS ========================= */
	
	public static void printStats()
	{
		LOG.info("========== ThreadPool Metrics ==========");
		LOG.info("Scheduled tasks: " + scheduledTasks.get());
		LOG.info("Instant tasks:   " + instantTasks.get());
		LOG.info("Rejected tasks:  " + rejectedTasks.get());
		
		for (int i = 0; i < _scheduledPools.length; i++)
			LOG.info("Scheduled #" + i + ": Active=" + _scheduledPools[i].getActiveCount() + " Queue=" + _scheduledPools[i].getQueue().size());
		
		for (int i = 0; i < _instantPools.length; i++)
			LOG.info("Instant #" + i + ": Active=" + _instantPools[i].getActiveCount() + " Queue=" + _instantPools[i].getQueue().size());
	}
	
	/* ========================= TASK WRAPPER ========================= */
	
	private static final class SafeTask implements Runnable
	{
		private final Runnable task;
		
		SafeTask(Runnable r)
		{
			task = r;
		}
		
		@Override
		public void run()
		{
			if (SHUTTING_DOWN)
				return;
			
			try
			{
				task.run();
			}
			catch (Throwable t)
			{
				LOG.warning("ThreadPool Task Error: " + t.getMessage());
			}
		}
	}
	
	public static long getRejectedTasks()
	{
		return rejectedTasks.get();
	}
	
}
