📘 L2J Mod Engine
Documentação Oficial para Desenvolvedores
1️⃣ O que é o L2J Mod Engine?

O L2J Mod Engine é um sistema que permite adicionar mods em Java puro ao GameServer sem modificar nem recompilar o core.

Os mods são:

º Compilados automaticamente na inicialização do servidor
º Carregados dinamicamente
º Totalmente desacoplados do core

🎯 Objetivo:
Facilitar a criação, manutenção e atualização de sistemas customizados (Voiced, Events, Balance, Features, etc).

data/mods/
 ├─ src/               → Código fonte dos mods (.java)
 │   └─ mods/
 │       └─ voiced/
 │           └─ MyVoicedCommand.java
 │
 ├─ bin/               → Classes compiladas automaticamente (.class)
 │
 └─ README.txt         → Esta documentação

⚠️ IMPORTANTE
ºNunca edite arquivos dentro de bin/
ºTudo deve ser feito em src/mods

3️⃣ Criando um Mod
3.1 Interface obrigatória

Todo mod DEVE implementar:

+	package mods.voiced;
+	import net.sf.l2j.gameserver.extension.L2JMod
+	
+	// Exemplo mínimo:
+	public class MyMod implements L2JMod
+	{
+	    @Override
+	    public void onLoad()
+	    {
+	        System.out.println("Meu mod carregado!");
+	    }
+	}

📌 O método onLoad() é chamado automaticamente durante o boot do GameServer.

4️⃣ Convenção de Package (MUITO IMPORTANTE)

O package deve refletir a estrutura de pastas.

Estrutura:
data/mods/src/mods/voiced/MyVoiced.java

Package correto:
package mods.voiced;

Se o package não bater com a pasta:

O mod não será carregado

Pode gerar erro de ClassNotFoundException

5️⃣ Exemplo Real: Voiced Command
Arquivo:
data/mods/src/mods/voiced/VoicedGainXpSp.java

package mods.voiced;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.extension.L2JMod;
import net.sf.l2j.gameserver.handler.IVoicedCommandHandler;
import net.sf.l2j.gameserver.handler.VoicedCommandHandler;
import net.sf.l2j.gameserver.model.actor.Player;

public class VoicedGainXpSp implements L2JMod, IVoicedCommandHandler
{
    private static final String[] COMMANDS = { "xpon", "xpoff" };

    @Override
    public void onLoad()
    {
        VoicedCommandHandler.getInstance().registerHandler(this);
        System.out.println("[Mod] Voiced XP ON/OFF carregado");
    }

    @Override
    public boolean useVoicedCommand(String command, Player player, String params)
    {
        switch (command)
        {
            case "xpon":
                player.setGainXpSpEnable(true);
                player.sendMessage("XP/SP ativado.");
                return true;

            case "xpoff":
                player.setGainXpSpEnable(false);
                player.sendMessage("XP/SP desativado.");
                return true;
        }
        return false;
    }

    @Override
    public String[] getVoicedCommandList()
    {
        return COMMANDS;
    }
}

6️⃣ Como funciona o carregamento

Durante o boot do servidor:

O engine escaneia data/mods/src/mods

Detecta arquivos .java

Compila apenas arquivos novos ou alterados

Remove .class órfãos (sem .java)

Carrega todas as classes compiladas

Executa onLoad() dos mods válidos

📌 Se um .java for deletado:

A classe compilada correspondente é automaticamente removida

O mod não será carregado

7				Atualizando um Mod
Cenários:

	Ação								Resultado
Editar .java					Recompila automaticamente
Criar novo .java					Compila e carrega
Deletar .java					Remove .class e desativa
Erro de compilação					Mod não carrega
Alterar mod							Reinicie o GameServer

⚠️ IMPORTANTE
O Java não permite descarregar classes em runtime.
Toda alteração exige restart do GameServer.

8️⃣ Boas Práticas (OBRIGATÓRIO LER)

✔️ Uma classe = um mod
✔️ Evite lógica pesada em onLoad()
✔️ Use Config.java para mensagens/configurações
✔️ Não acesse arquivos fora do necessário
✔️ Código malicioso pode quebrar o servidor

🚨 Mods têm acesso TOTAL ao servidor
Use apenas código confiável.

9️⃣ Erros Comuns

❌ Package não bate com a pasta
❌ Classe não implementa L2JMod
❌ Dependência inexistente
❌ Erro de compilação Java
❌ Usar JRE em vez de JDK

📌 O servidor DEVE rodar com JDK.

🔚 Conclusão

O L2J Mod Engine foi criado para:

Facilitar desenvolvimento

Evitar alterações no core

Permitir modularidade real

Manter o servidor limpo e organizado

Se você sabe Java, você sabe criar mods.