# Spark

## Intro

O projeto ACME demanda cálculo em uma grande quantidade de dados. Para que seja sempre escalável, este tipo de processamento deve ser distribuído e contamos com várias soluções que atendem este tipo de arquitetura sendo a mais utilizada atualmente (2015), o Apache Spark. 

A arquitetura do Apache Spark pode ser resumida da seguinte forma:

1. Cada instância recebe seus dados e o trabalho que deve executar e executa em várias threads até que o trabalho esteja finalizado. Além de distribuir o trabalho temos o benefício de isolar o processamento em cada uma das instâncias. Contudo, dados compartilhados devem ter uma camada externa e compartilhada de armazenamento. 

2. O gerenciamento do cluster é agnostico, dado que este adquira um processo de execução e este comunique com outros nós.

3. O programa driver (programa com o trabalho a ser executado) se comunica com os executores durante o tempo de vida da aplicação. Desta forma, o programa driver pode ser acessado pelos nós dos executores.

4. Dado que o driver agenda trabalhos no cluster, de preferência este deve trabalhar na mesma rede que outros nós do cluster.

Para mais informações sobre o Apache Spark, veja a seção específica mais abaixo.

## Install

### Standalone

- Download, unzip and execute.

### Cluster

1. Instalar o JDK, M2_HOME, SPARK e atribuir as seguintes variáveis de ambiente:
 
	export JAVA_HOME=/usr/lib/jvm/java-7-oracle
	export M2_HOME=/home/vagrant/apache-maven-3.3.3
	export SPARK_HOME=/home/vagrant/spark

2. Aumentar a memória utilizada pelo Maven:
 
	export MAVEN_OPTS="-Xmx2g -XX:MaxPermSize=512M -XX:ReservedCodeCacheSize=512m"

2. Faça o build do spark através do comando abaixo:
 
	cd $SPARK_HOME
	$M2_HOME/bin/mvn -DskipTests clean package
	
3. Gerar o acesso SSH (password-less):

No master, criar a chave:

	$ ssh-keygen -t dsa
	Generating public/private dsa key pair.
	Enter file in which to save the key (/home/vagrant/.ssh/id_dsa): 
	Enter passphrase (empty for no passphrase): 
	Enter same passphrase again: 
	Your identification has been saved in /home/vagrant/.ssh/id_dsa.
	Your public key has been saved in /home/vagrant/.ssh/id_dsa.pub.
	The key fingerprint is:
	38:2f:53:c9:6f:e5:0e:00:ee:52:9e:86:a0:de:76:00 vagrant@precise64
	The key's randomart image is:
	+--[ DSA 1024]----+
	|                 |
	|                 |
	|      .          |
	| E   . + .       |
	|  o   = S   .    |
	| . o = = o o     |
	|.   + B . + .    |
	|. .. + o . o     |
	| ....       .    |
	+-----------------+

Copiar a chave publica (id_dsa.pub) do master para os workers e executar os seguintes comandos:

	scp ~/.ssh/id_dsa.pub vagrant@192.168.33.21:/home/vagrant/.ssh/.

Nos workers:

	cat ~/.ssh/id_dsa.pub >> ~/.ssh/authorized_keys

4. No master, editar $SPARK_HOME/conf/slaves e adicionar cada uma das máquinas do slave:
 
	localhost
	192.168.33.20
	192.168.33.21

5. Start cluster executing at master:
 	
	$SPARK_HOME/sbin/start-all

6. Stop cluster executing at master:
 
	$SPARK_HOME/sbin/stop-all

By hand, on master:

	$SPARK_HOME/bin/spark-class org.apache.spark.deploy.master.Master
	
By hand, on workers:

	$SPARK_HOME/bin/spark-class org.apache.spark.deploy.worker.Worker spark://10.0.2.15:7077

### Logging

Para alterar o nível de log das instâncias do spark, edite o arquivo $SPARK_HOME/conf/log4j.properties.

**NOTA** Este arquivo por padrão não existe mas a instalação conta com um arquivo template. Faça uma cópia deste template para o nome log4j.properties.

### Executando programas

Para executar um programa de forma local, basta _submeter_ o programa indicando o parametro _local_ e o número de cores desejados. No exemplo abaixo os dados que serão utilizados estão no arquivo data/leipzig1M.txt.

	spark-submit 
		--master local[4] 
		--class "demo.spark.HelloSpark" --jars "lib/log4j-1.2.16.jar,lib/slf4j-api-1.7.12.jar" 
		target/hello-0.0.1-SNAPSHOT.jar data/leipzig1M.txt

Sendo que:

local    - run in local mode using a single core
local[n] - run in local mode using n cores
local[*] - run in local mode using as many cores the machine supports

Logo, para executar um programa de forma distribuída no cluster, basta _submeter_ o programa indicando o master node e parâmetros de distribuição como por exemplo o total de memória que cada worker irá alocar para os dados que serão enviados.

	spark-submit 
		--master spark://host:7077 
		--executor-memory 10g 
		--class "demo.spark.HelloSpark" 
		target/hello-0.0.1-SNAPSHOT.jar data/leipzig1M.txt

