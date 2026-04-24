# 📦 DEV Project - Instalação e Execução

Se você baixou o pacote do servidor e está com dúvidas de como executar, siga este passo a passo simples:

---

## 📦 Downloads Necessários

| Componente   | Download |
|--------------|----------|
| 🔧 Client Files (System, Animations, SysTextures...) | [Download Client](https://github.com/JulioPradoL2j/L2JDev-Patch) |
| ☕ Java 11 | [Download Java 11](https://mega.nz/file/V7tj1arS#OKWaTzaCqYK0m3iMmR0kW3TddfAJoiu8a20kOFEKShk) |
| 🛢️ MariaDB 10.4 | [Download MariaDB](https://mega.nz/file/1jEykRgL#DDuIGktiFbmE-M0jMzhUvYVckw1U0ov-OnZEYS5vopU) |

---

## 1️⃣ Instalar o Java 11

Antes de tudo, é necessário instalar o Java 11:

- Baixe o [Java 11](https://mega.nz/file/V7tj1arS#OKWaTzaCqYK0m3iMmR0kW3TddfAJoiu8a20kOFEKShk)
- Instale normalmente.

Após a instalação, **crie a variável de ambiente `JAVA_HOME`:**

### No Windows:

- Vá em: `Painel de Controle > Sistema > Configurações Avançadas > Variáveis de Ambiente`
- Clique em **Nova (variável do sistema)**
  - Nome: `JAVA_HOME`
  - Valor: `C:\Program Files\AdoptOpenJDK\jdk-11.0.10.9-hotspot` _(ajuste conforme o local de instalação)_
- Edite a variável `Path` e adicione: `%JAVA_HOME%\bin`

### No Linux:

- Adicione no seu `~/.bashrc` ou `~/.zshrc`:
```bash
export JAVA_HOME=/caminho/para/AdoptOpenJDK/jdk-11.0.10.9-hotspot
export PATH=$JAVA_HOME/bin:$PATH
```

---

## 2️⃣ Instalar o MariaDB

- Baixe e instale o [MariaDB](https://mega.nz/file/1jEykRgL#DDuIGktiFbmE-M0jMzhUvYVckw1U0ov-OnZEYS5vopU)
- Durante a instalação, defina:
  - **Usuário**: `root`
  - **Senha**: `root`

---

## 3️⃣ Rodar o banco de dados

Após a instalação do MariaDB:

- Vá até a pasta `tools/` do projeto
- Execute o script correspondente ao seu sistema operacional para importar as tabelas e dados iniciais:

### ✅ No **Windows**:
```bat
install_db.bat
```

### ✅ No **Linux**:
```bash
chmod +x install_db.sh
./install_db.sh
```

> Você também pode usar um cliente como HeidiSQL, DBeaver ou MySQL Workbench para importar manualmente os arquivos `.sql` da pasta `tools/sql`.

---

## 4️⃣ Importar o projeto no Eclipse

- Abra o **Eclipse IDE**
- Vá em: `File > Import > Existing Projects into Workspace`
- Selecione a pasta raiz do projeto
- Após a importação, procure pela pasta `launcher/`

Dentro dela estão os arquivos:

- `LoginServer.launch`
- `GameServer.launch`

Execute o servidor clicando com o botão direito no `.launch` desejado e escolha:  
**Run As > Java Application**

---

## 5️⃣ Compilar o pacote manualmente

Se quiser compilar o projeto fora do Eclipse:

- Use o `build.xml` com o EclipesIDE para compilar o código-fonte
  - Ele irá gerar os `.class` e `.jar` necessários
- Depois use o `amount.xml` para:
  - Agrupar LoginServer, GameServer, Imgs, Tools e libs
  - Criar uma pasta `Zip/` com todos os arquivos prontos para execução

---

## 📌 Observações

- O projeto requer Java 11 especificamente — versões superiores podem gerar incompatibilidades.
- Certifique-se de que o MariaDB está com o serviço ativo antes de rodar o script.

---

## ❓ Problemas comuns / Common Issues

- ERRO: "mysql: command not found"
  ➤ Solução: Verifique se o MySQL/MariaDB está instalado e adicionado ao PATH.

- ERRO: Acesso negado para 'root'
  ➤ Solução: Altere a senha no script ou use um usuário com permissão.

- Nenhuma tabela é criada
  ➤ Solução: Verifique se os arquivos .sql estão realmente dentro da pasta /sql.

---

## 📬 Suporte

Caso tenha dificuldades, poste sua dúvida no fórum ou envie uma mensagem com:
- Print do erro
- Sistema operacional
- O que tentou fazer
- juliopradrol2j@gmail.com
Obrigado por usar o projeto L2JDev!