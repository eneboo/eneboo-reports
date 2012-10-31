#!/bin/bash
clear
echo -e "\nUtilidad de empaquetado de librería Eneboo Reports y dependencias"
echo -e "(C) 2012 José A. Fernández Fernández <aullasistemas@gmail.com>\n"


echo -e "\n* Copiando .jar necesarios para compilar en ./temp/"
mkdir -p temp
cp lib/jasperreports* temp
cp lib/postgresql* temp

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
cp temp/*.class .
jar -cmvf manifiesto.txt enebooreports.jar enebooreports.class Xpm.class otros/* >>/dev/null
echo -e "\n* Generando EnebooReports.zip"
zip EnebooReports.zip lib/* enebooreports.jar >> /dev/null
echo -e "\n* Limpiando"
rm -fr temp
rm -f *.class
rm -f *.jar
rm -f *.txt
echo "¡¡ PROCESO TERMINADO !!"
