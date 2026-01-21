Plungs - L2J Extension System
=============================

O Plungs é um sistema de extensões (plugins) para servidores L2J.
Ele permite adicionar funcionalidades personalizadas ao servidor
SEM alterar o core original.

------------------------------------------------------------
COMO FUNCIONA
------------------------------------------------------------

Ao iniciar o GameServer, o Plungs:

1. Procura esta pasta (/plungs)
2. Localiza arquivos com a extensão: .plungs.jar
3. Carrega automaticamente a extensão encontrada
4. Executa o método init() da extensão

Cada plugin é isolado em seu próprio JAR.

------------------------------------------------------------
ESTRUTURA DE UM PLUGIN
------------------------------------------------------------

Regras obrigatórias:

- O arquivo DEVE terminar com: .plungs.jar
- Apenas 1 plugin por JAR
- O plugin deve implementar a interface:
  net.sf.l2j.gameserver.extension.L2JExtension
- A classe principal deve possuir construtor publico e vazio

Exemplos de nome de arquivo:
- hello.plungs.jar
- customBoss.plungs.jar

------------------------------------------------------------
CLASSE PRINCIPAL
------------------------------------------------------------

Exemplo de plugin valido:

package mods;

import net.sf.l2j.gameserver.extension.L2JExtension;

public class HelloPlugin implements L2JExtension
{
    @Override
    public String getName()
    {
        return "HelloPlugin";
    }

    @Override
    public void init()
    {
        System.out.println("[HelloPlugin] Plugin iniciado com sucesso!");
    }
}

------------------------------------------------------------
METODOS OBRIGATORIOS
------------------------------------------------------------

Todo plugin DEVE implementar:

- String getName()
  -> Nome unico do plugin

- void init()
  -> Executado automaticamente ao iniciar o servidor

------------------------------------------------------------
CICLO DE VIDA
------------------------------------------------------------

- O carregamento ocorre apenas na inicializacao do servidor
- Alteracoes exigem reiniciar o GameServer
- Plugins nao podem ser recarregados em runtime (por enquanto)

------------------------------------------------------------
OBSERVACOES IMPORTANTES
------------------------------------------------------------

- Erros em um plugin NAO impedem o servidor de iniciar
- Plugins invalidos serao ignorados
- Logs de erro aparecem no console com o prefixo [Plungs]

------------------------------------------------------------
DICA
------------------------------------------------------------

Use o Plungs para:
- Eventos personalizados
- NPCs customizados
- Sistemas PvP / PvE
- Regras exclusivas do seu servidor

------------------------------------------------------------
Fim da documentacao.
