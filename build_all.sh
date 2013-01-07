#!/bin/bash
clear
echo -e "\nUtilidad de empaquetado de librería Eneboo Reports y dependencias"
echo -e "(C) 2012 José A. Fernández Fernández <aullasistemas@gmail.com>\n"

for jasperfile in lib/jasperreports-applet-*.jar
  do
    BUILDJASPER="${jasperfile#lib/jasperreports-applet-}"
    BUILDJASPER="${BUILDJASPER/.jar/}"
  done

BUILD=$(date +%Y%m%d)
NOMBREZIP="EnebooReports-$BUILDJASPER-$BUILD.zip"

echo -e "\n\n Eneboo Reports Build: $BUILD \n Versión Jasper Library: $BUILDJASPER" 
echo -e "\n* Copiando .jar necesarios para compilar en ./temp/"
mkdir -p temp
cp lib/jasperreports* temp

echo -e "\n* Descomprimiendo .jar de ./temp/"
cd temp
for ficheros in *.jar
  do
  echo "- $ficheros"
  jar xf $ficheros
  done

cd ..
echo -e "\n* Copiando ficheros .java en ./temp/"
cp fuentes/*.java temp

echo -e "\n* Generando version.java"
echo "public class jrversion {" >> temp/jrversion.java
echo "public static String jasper() {" >> temp/jrversion.java
echo -e "return \"$BUILDJASPER\";" >> temp/jrversion.java
echo "}" >> temp/jrversion.java
echo "public static String eReports() {" >> temp/jrversion.java
echo -e "return \"$BUILD\";" >> temp/jrversion.java
echo "}" >> temp/jrversion.java
echo "}" >> temp/jrversion.java

echo -e "\n* Compilando ... "
cd temp
for ficheros in *.java
  do
  echo "- $ficheros"
  javac $ficheros >> /dev/null
  done

echo -e "\n* Generando manifiesto actualizado"
cd ../lib
PRIMERO="0"
for ficheros in *.jar
  do
  if [ $PRIMERO == "0" ]
  	then
  	echo "Main-Class: enebooreports" >> ../manifiesto.txt
    	echo "Class-Path: lib/$ficheros" >> ../manifiesto.txt
    	PRIMERO="1"
    	else
    	echo "  lib/$ficheros" >> ../manifiesto.txt
    	fi
  done

echo -e "\n* Generando enebooreports.jar"
cd ..
cp temp/enebooreports.class .
cp temp/ERUtil.class .
cp temp/Xpm.class .
cp temp/jrversion.class .

for ficheros in *.class
  do
  CLASSES+=" $ficheros"
  done
jar -cmvf manifiesto.txt enebooreports.jar$CLASSES otros/* >>/dev/null
echo -e "\n* Generando $NOMBREZIP"
zip $NOMBREZIP lib/* enebooreports.jar >> /dev/null

echo -e "\n* Limpiando"
rm -fr temp
rm -f *.class
rm -f *.jar
rm -f *.txt
rm -f lib/*.tmp
echo -e "\n\n¡¡ PROCESO TERMINADO !!\nEl fichero $NOMBREZIP está listo para ser distribuido.\n\n"
