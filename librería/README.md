Necesitamos:

Java 7 JDK (de OpenJava,ORACLE ... )


* Copiamos el contenido de carpeta sources (*.java) en carpeta tree

cd sources

cp *.java ../tree


* Compilamos ficheros .java

cd ../tree

javac enebooreports.java

javac Xpm.java


* Creamos el .jar

jar -cmvf manifiesto.txt enebooreports.jar *


Listo !!
