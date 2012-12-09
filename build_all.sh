#!/bin/bash
clear
echo -e "\nUtilidad de empaquetado de librería Eneboo Reports y dependencias"
echo -e "(C) 2012 José A. Fernández Fernández <aullasistemas@gmail.com>\n"

BUILD=$(date +%Y%m%d)

echo -e "\n* Copiando .jar necesarios para compilar en ./temp/"
mkdir -p temp
cp lib/jasperreports* temp
cp lib/common* temp

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

#mkdir net/sf/jasperreports/view



echo -e "\n* Añadiendo modificaciones a JasperLibrary ..."

for ficheros in ../lib/jasperreports*.jar
  do
  echo "- $ficheros"
  cp SaveContributorUtils.class net/sf/jasperreports/view/SaveContributorUtils.class
  jar -uf $ficheros net/sf/jasperreports/view/SaveContributorUtils.class 
  cp JRViewer.class net/sf/jasperreports/view/JRViewer.class
  jar -uf $ficheros net/sf/jasperreports/view/JRViewer.class 
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

for ficheros in *.class
  do
  CLASSES+=" $ficheros"
  done
jar -cmvf manifiesto.txt enebooreports.jar$CLASSES otros/* >>/dev/null
echo -e "\n* Generando EnebooReports-$BUILD.zip"
zip EnebooReports-$BUILD.zip lib/* enebooreports.jar >> /dev/null
echo -e "\n* Reponiendo enebooreports*.jar modificados"
cd temp
for ficheros in jasperreports*.jar
  do
  echo "- $ficheros"
   cp -f $ficheros ../lib
  done
cd ..
echo -e "\n* Limpiando"
rm -Rfr temp
rm -f *.class
rm -f *.jar
rm -f *.txt
echo -e "\n\n¡¡ PROCESO TERMINADO !!\nEl fichero EnebooReports-$BUILD.zip está listo para distribuir."
