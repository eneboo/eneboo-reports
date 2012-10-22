#!/bin/bash
clear
echo -e "\nUtilidad de empaquetado de librería Eneboo Reports y dependencias"
echo -e "(C) 2012 José A. Fernández Fernández <aullasistemas@gmail.com>\n"


echo "* Copiando .jar necesarios para compilar en ./temp/"
mkdir -p temp
cp lib/jasperreports* temp
echo "* Copiando ficheros .java en ./temp/"
cp fuentes/*.java temp

echo "* Descomprimiendo .jar de ./temp/"
cd temp
jar xf jasperreports-4.7.1.jar
jar xf jasperreports-applet-4.7.1.jar
jar xf jasperreports-fonts-4.7.1.jar
jar xf jasperreports-javaflow-4.7.1.jar
echo "* Compilando enebooreports.java"
javac enebooreports.java >> /dev/null
echo "* Compilando Xpm.java"
javac -Xlint:unchecked Xpm.java >> /dev/null
echo "* Generando manifiesto actualizado"
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

echo "* Generando enebooreports.jar"
cd ..
cp temp/*.class .
jar -cmvf manifiesto.txt enebooreports.jar enebooreports.class Xpm.class otros/* >>/dev/null
echo "* Generando EnebooReports.zip"
mkdir fonts
echo "Añade es este directorio las tipografías especiales que usen tus reports." >> fonts/README.txt
zip EnebooReports.zip lib/* fonts/* enebooreports.jar >> /dev/null
echo "* Limpiando"
rm -fr temp
rm -f *.class
rm -f *.jar
rm -f *.txt
rm -fr fonts
echo "¡¡ PROCESO TERMINADO !!"
