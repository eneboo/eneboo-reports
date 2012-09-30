Necesitamos:

* Java OpenJDK


- Copiamos el contenido de carpeta sources (*.java) en carpeta tree

cp *.java ../tree


- Compilamos ficheros .java

javac enebooreports.java

javac Xpm.java


- Creamos el .jar

jar -cmvf manifiesto.txt enebooreports.jar *


nos creará el enebooreports.jar
