package net.sf.l2j.gameserver.extension;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import net.sf.l2j.gameserver.extension.log.PlugsLogger;

public final class ExtensionBootstrap
{
	private static final File EXT_DIR = new File("../plungs");
	private static final File README = new File(EXT_DIR, "README.txt");
	
	private static final Map<String, L2JExtension> PLUGINS = new LinkedHashMap<>();
	
	private ExtensionBootstrap()
	{
	}
	
	public static void load()
	{
		PlugsLogger.header();
		
		if (!EXT_DIR.exists())
		{
			EXT_DIR.mkdirs();
			createReadme();
			PlugsLogger.warn("Pasta /plungs criada. Nenhum plugin carregado.");
			return;
		}
		
		File[] jars = EXT_DIR.listFiles(f -> f.isFile() && f.getName().endsWith(".plungs.jar"));
		
		if (jars == null || jars.length == 0)
		{
			PlugsLogger.warn("Nenhum plugin encontrado.");
			return;
		}
		
		PlugsLogger.info("Carregando plugins...");
		
		for (File jar : jars)
		{
			try
			{
				loadPlugin(jar);
			}
			catch (Exception e)
			{
				PlugsLogger.error("Falha ao carregar: " + jar.getName(), e);
			}
		}
		
		PlugsLogger.info("Total de plugins carregados: " + PLUGINS.size());
	}
	
	private static void loadPlugin(File jarFile) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		URLClassLoader cl = new URLClassLoader(new URL[]
		{
			jarFile.toURI().toURL()
		}, ExtensionBootstrap.class.getClassLoader());
		
		try (JarFile jar = new JarFile(jarFile))
		{
			Enumeration<JarEntry> entries = jar.entries();
			
			while (entries.hasMoreElements())
			{
				JarEntry entry = entries.nextElement();
				
				if (!entry.getName().endsWith(".class"))
					continue;
				
				String className = entry.getName().replace('/', '.').replace(".class", "");
				
				Class<?> clazz = Class.forName(className, false, cl);
				
				if (!L2JExtension.class.isAssignableFrom(clazz))
					continue;
				
				L2JExtension ext = (L2JExtension) clazz.getDeclaredConstructor().newInstance();
				ext.init();
				
				PLUGINS.put(ext.getName(), ext);
				
				PlugsLogger.info("Plugin carregado: " + ext.getName() + " (" + jarFile.getName() + ")");
				return; 
			}
		}
	}
	
	private static void createReadme()
	{
		try (FileWriter w = new FileWriter(README))
		{
			w.write(
				"Plungs - L2J Extension System\n" +
				"=============================\n\n" +

				"O Plungs é um sistema de extensões (plugins) para servidores L2J.\n" +
				"Ele permite adicionar funcionalidades personalizadas ao servidor\n" +
				"SEM alterar o core original.\n\n" +

				"------------------------------------------------------------\n" +
				"COMO FUNCIONA\n" +
				"------------------------------------------------------------\n\n" +

				"Ao iniciar o GameServer, o Plungs:\n\n" +
				"1. Procura esta pasta (/plungs)\n" +
				"2. Localiza arquivos com a extensão: .plungs.jar\n" +
				"3. Carrega automaticamente a extensão encontrada\n" +
				"4. Executa o método init() da extensão\n\n" +

				"Cada plugin é isolado em seu próprio JAR.\n\n" +

				"------------------------------------------------------------\n" +
				"ESTRUTURA DE UM PLUGIN\n" +
				"------------------------------------------------------------\n\n" +

				"Regras obrigatórias:\n\n" +
				"- O arquivo DEVE terminar com: .plungs.jar\n" +
				"- Apenas 1 plugin por JAR\n" +
				"- O plugin deve implementar a interface:\n" +
				"  net.sf.l2j.gameserver.extension.L2JExtension\n" +
				"- A classe principal deve possuir construtor publico e vazio\n\n" +

				"Exemplos de nome de arquivo:\n" +
				"- hello.plungs.jar\n" +
				"- customBoss.plungs.jar\n\n" +

				"------------------------------------------------------------\n" +
				"CLASSE PRINCIPAL\n" +
				"------------------------------------------------------------\n\n" +

				"Exemplo de plugin valido:\n\n" +

				"package mods;\n\n" +
				"import net.sf.l2j.gameserver.extension.L2JExtension;\n\n" +
				"public class HelloPlugin implements L2JExtension\n" +
				"{\n" +
				"    @Override\n" +
				"    public String getName()\n" +
				"    {\n" +
				"        return \"HelloPlugin\";\n" +
				"    }\n\n" +
				"    @Override\n" +
				"    public void init()\n" +
				"    {\n" +
				"        System.out.println(\"[HelloPlugin] Plugin iniciado com sucesso!\");\n" +
				"    }\n" +
				"}\n\n" +

				"------------------------------------------------------------\n" +
				"METODOS OBRIGATORIOS\n" +
				"------------------------------------------------------------\n\n" +

				"Todo plugin DEVE implementar:\n\n" +
				"- String getName()\n" +
				"  -> Nome unico do plugin\n\n" +
				"- void init()\n" +
				"  -> Executado automaticamente ao iniciar o servidor\n\n" +

				"------------------------------------------------------------\n" +
				"CICLO DE VIDA\n" +
				"------------------------------------------------------------\n\n" +

				"- O carregamento ocorre apenas na inicializacao do servidor\n" +
				"- Alteracoes exigem reiniciar o GameServer\n" +
				"- Plugins nao podem ser recarregados em runtime (por enquanto)\n\n" +

				"------------------------------------------------------------\n" +
				"OBSERVACOES IMPORTANTES\n" +
				"------------------------------------------------------------\n\n" +

				"- Erros em um plugin NAO impedem o servidor de iniciar\n" +
				"- Plugins invalidos serao ignorados\n" +
				"- Logs de erro aparecem no console com o prefixo [Plungs]\n\n" +

				"------------------------------------------------------------\n" +
				"DICA\n" +
				"------------------------------------------------------------\n\n" +

				"Use o Plungs para:\n" +
				"- Eventos personalizados\n" +
				"- NPCs customizados\n" +
				"- Sistemas PvP / PvE\n" +
				"- Regras exclusivas do seu servidor\n\n" +

				"------------------------------------------------------------\n" +
				"Fim da documentacao.\n"
			);
		}
		catch (IOException e)
		{
			PlugsLogger.error("Falha ao criar README.txt", e);
		}
	}


	
	public static void list()
	{
		if (PLUGINS.isEmpty())
		{
			PlugsLogger.warn("Nenhum plugin ativo.");
			return;
		}
		
		PlugsLogger.info("Plugins ativos:");
		PLUGINS.keySet().forEach(p -> System.out.println(" - " + p));
	}
}
