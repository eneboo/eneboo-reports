import java.io.*; //include Java's standard Input and Output routines
import java.sql.*;
import java.util.*;
import javax.swing.*;
import java.lang.ClassLoader;
import java.lang.Throwable;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.PrinterJob;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import javax.print.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.*;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporterParameter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
import net.sf.jasperreports.engine.data.JRXmlDataSource;
import org.apache.commons.codec.digest.DigestUtils;




public class enebooreports {
 
public static String ficheroTemp, fileTempCloud;
public static Connection conn;
//public static String driverSQL;
public static String build = "Build " + jrversion.eReports();
public static String versionJR = jrversion.jasper();
public static splash splash = new splash();
                public static void main(String[] args) throws IOException {     
			try {
			    splash.mostrar(); //Mostramos splash
			    String ficheroTemp;
                            String impresora,cloudID;
                           // String changelog = "";
                            Image img;
			    Boolean pdf,impDirecta, guardaTemporal, modoCloud;
			    int nCopias, nParametrosJasper;
			    long start;
			    Class.forName(args[0]);
			    //JOptionPane.showMessageDialog(null, "Init iniciado" , "Eneboo Reports", 1);
			    InputStream is=enebooreports.class.getClass().getResourceAsStream("/otros/init.jasper");
			    JasperReport creditos = (JasperReport) JRLoader.loadObject(is);
			    JasperReport report = creditos;

			    // Cargamos changelog
			   // InputStream chl = enebooreports.class.getResourceAsStream("/otros/changelog");
			    
			    
			    //PopupMenu popMenu= new PopupMenu();
 			    //MenuItem salir = new MenuItem("Salir");
 			    //popMenu.add(salir);
 			    
 			    //if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0)
 			    //	{
  	                    //	img = new javax.swing.ImageIcon(enebooreports.class.getClass().getResource("/otros/logo16.gif")).getImage();
  	                    //	}
  	                    //	else
  	                    //	{
  	                    //	img = new javax.swing.ImageIcon(enebooreports.class.getClass().getResource("/otros/logo24.gif")).getImage();
  	                    //	}
 	                   //TrayIcon trayIcon = new TrayIcon(img, "Eneboo Reports", popMenu);
 	                   //SystemTray.getSystemTray().add(trayIcon);
 	                               
 	                   

			    
     			   // BufferedReader br2 = new BufferedReader(new InputStreamReader(chl));
     			   // String line= null;
     			   // String listadoCompleto = "";
     			   // while (null != (line = br2.readLine())) {
        		   //					 listadoCompleto = listadoCompleto + line + "<br>";
      			   //	        			    }  

   			   // enebooreports.driverSQL = args[1];
   			   if (enebooreports.conn != null) //Si existe,cerramos la conexión previa
           			enebooreports.conn.close();
                            enebooreports.conn = DriverManager.getConnection(args[1],args[2],args[3]);
                            //JOptionPane.showMessageDialog(null, "Init finalizado" , "Eneboo Reports", 1);
			    do
                              {
                            BufferedReader stdin = new BufferedReader (new InputStreamReader(System.in));
			    System.out.flush();// empties buffer, before you input text
			    ficheroTemp =""; //Nombre fichero Temporal
          		    ficheroTemp = stdin.readLine();
			    if (ficheroTemp == null ) System.exit(0);
			    
			    splash.mostrar(); //Si el break anterior no cierra la libreria , mostramos splash.
			    enebooreports.ficheroTemp = ficheroTemp;
			    start = System.currentTimeMillis(); /* Para controlar el tiempo */					
                            guardaTemporal = false; //bool que indica si se borra o no el temp al finalizar de usarlo.
                            modoCloud = false;
                            cloudID = "";
                            guardaTemporal = Boolean.parseBoolean(stdin.readLine());
		            pdf = false; // exporta a pdf
                            pdf = Boolean.parseBoolean(stdin.readLine());
		            nCopias = 0; // Número de copias
                            nCopias = Integer.parseInt(stdin.readLine());
			    impresora =""; // nombre de impresora
                            impresora = stdin.readLine();
			    impDirecta = false; // impresión directa
                            impDirecta = Boolean.parseBoolean(stdin.readLine());
                            nParametrosJasper = 0; // Número de parametros que vienen (Pareja Nombre-Valor)
                            nParametrosJasper = Integer.parseInt(stdin.readLine());
                            String[] parametroNombre = new String[nParametrosJasper]; 
                            String[] parametroValor = new String[nParametrosJasper];
                          				  for(int i = 0; i < nParametrosJasper; i++ ) {
	 									parametroNombre[i] = stdin.readLine();
	 									parametroValor[i] = stdin.readLine();  
													}

                        java.util.Map<String, Object> hm = new HashMap<String,Object>(); //INICIALIZO MAPA    				
			if (ficheroTemp.equals( "version" )) 
						{
						report = creditos;
						guardaTemporal = true;//Para no intentar borrar luego un fichero que no existe
						hm.put("VERSION", enebooreports.build);
						//hm.put("CHANGELOG",listadoCompleto);
						hm.put("VERSIONJR",enebooreports.versionJR);						
						}
					else {     
                          	       		if (ficheroTemp.equals( "Repetir" ))//Solo compilar si no se llama repetir.
                                       				guardaTemporal = true; //Para no intentar borrar luego un fichero que no existe
                                       			 		 else 
                                        		 	report = JasperCompileManager.compileReport(ficheroTemp);

	                                       for(int j = 0; j < nParametrosJasper; j++ )
                         				  if(!parametroValor[j].equals( "") && !parametroNombre[j].equals( ""))
                         				  	{
	 						 		if (parametroNombre[j].equals("REPORT_LOCALE"))
	 						 				{
	 						 				hm.put(parametroNombre[j],new java.util.Locale(parametroValor[j]));
	 						 				}
	 						 			else
	 						 				{
	 						 				hm.put(parametroNombre[j], parametroValor[j]); //Seteamos Parametros en mapa
	 						 				}
					      			if(parametroNombre[j].equals("X2CANVAS")) //Comprobamos si estamos en modo cloud
					      				{
					      				cloudID = parametroValor[j];
					      				modoCloud = true;
					      				}
					      			
					      			
					      			}
					      }
					      
					      
					if (modoCloud)
						{
						pdf = true;
						fileTempCloud = DigestUtils.shaHex(String.valueOf(System.currentTimeMillis()));
						impresora = "/downloads/"+ fileTempCloud + ".pdf";//Ruta de la impresora
												
						}	
					
					
					JasperPrint print = JasperFillManager.fillReport(report, hm, enebooreports.conn); //Rellenamos el report compilado
					if (impDirecta) 
							{
							impresionDirecta( impresora, nCopias, print );
							splash.ocultar();
							}
							else
					 		if(pdf) 
					 			{
					 			JasperExportManager.exportReportToPdfFile(print, impresora); // Exporta el informe a PDF
					 			
					 			File file = new File(impresora);
								int nIntentos = 0;
								
								while (!file.exists() && nIntentos <= 100) {
    									try { 
        									Thread.sleep(100);
        									nIntentos++;
    									    } catch (InterruptedException ie) { /* safe to ignore */ }
								}

					 			if(modoCloud && file.exists())
					 				{
					 				Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
									StringSelection ss = new StringSelection(fileTempCloud + ".pdf_" + cloudID);
									cb.setContents(ss, ss);
					 				}
					 				else 
									if (!file.exists())
					 						{
											splash.ocultar();
					 						JOptionPane.showMessageDialog(null, "Se ha producido un problema al generar el pdf." , "Eneboo Reports", 1);
					 						}
								splash.ocultar();					 			

								}
								          else
								          	{
								          	splash.ocultar();
								          	//java.awt.Toolkit.getDefaultToolkit().beep();
								          	if (!mostrarVisor( print, build))
								          		JOptionPane.showMessageDialog(null, "El Visor sufrió un problema." , "Eneboo Reports", 1);
								          	}		  	       
				if (!guardaTemporal)
						{
                                   		File ficheroT = new File(ficheroTemp);
                                    		if (!ficheroT.delete())
                                         		JOptionPane.showMessageDialog(null, "El fichero Temporal " + ficheroTemp + " no se puede borrar." , "Eneboo Reports", 1);
						}
 				} while (!ficheroTemp.equals( "version" ));
 			splash.cerrar(); //Eliminamos la instancia del splash	
 			System.exit(0);
			}
			catch (Exception e) {
			 splash.cerrar(); //Eliminamos la instancia del splash
			 crearLogError(e);       		
		       }  
		
	}
	
	
	
	
	
	
	
	
	
	
	public static Boolean mostrarVisor(JasperPrint print, String build) 
									{
							try {
									JasperViewer viewer = new JasperViewer(print, false);
                                    					viewer.setTitle(print.getName() + " - Eneboo Reports"); 
									viewer.setIconImage(new javax.swing.ImageIcon(enebooreports.class.getClass().getResource("/otros/logo32.gif")).getImage());
									viewer.setVisible(true);
									try {
										viewer.setAlwaysOnTop(true);
										viewer.setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
										viewer.setAlwaysOnTop(false); //Ahora no interesa estar siempre 
										while (viewer.isShowing())
										  	Thread.sleep(500);	//Esperamos a que el visor se cierre
										//viewer.dispose();
										
									 } finally
										{
										//java.awt.Toolkit.getDefaultToolkit().beep();
										//viewer.dispose();
										return true;
										}							   
								    	}
					 catch (Exception e) {  
            JOptionPane.showMessageDialog(null, "mostrarVisor :: Se ha producido un error (Exception) : \n " + e.toString() , "Eneboo Reports", 1);
	    e.printStackTrace();
		crearLogError(e);
		return false;
		       }
		       }  
	
	
				
					
					
	public static void impresionDirecta(String impresora, int nCopias, JasperPrint print) {
	
		try
		{
		JRPrintServiceExporter exporter = new JRPrintServiceExporter();
		//Aqui imprimimos directamente en var impresora...
		//JasperPrint print = JasperFillManager.fillReport( this.class.getResource("/classpath/yourReport.jasper").getPath(), new HashMap(), new yourReportDataSource());
		PrinterJob job = PrinterJob.getPrinterJob();
		/* Create an array of PrintServices */
		PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
		int selectedService = -1;
		String listadoImpresorasDisponibles = "";
		/* Scan found services to see if anyone suits our needs */
		for(int i = 0; i < services.length;i++){
				if(services[i].getName().contains(impresora))
						{
						job.setPrintService(services[i]);
						selectedService = i;	
						}
				listadoImpresorasDisponibles += services[i].getName() + "\n";
							}
		if (listadoImpresorasDisponibles.equals("")) listadoImpresorasDisponibles = "¡¡ Opppps !! . No se han detectado impresoras en el sistema";
		if ( selectedService > -1) 
			{
			PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
			//MediaSizeName mediaSizeName = MediaSize.findMedia(4,4,MediaPrintableArea.INCH);
			//printRequestAttributeSet.add(mediaSizeName);
			printRequestAttributeSet.add(new Copies(nCopias)); // *************** Numero de copias
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
			/* We set the selected service and pass it as a paramenter */
			exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE, services[selectedService]);
			exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, services[selectedService].getAttributes());
			exporter.setParameter(JRPrintServiceExporterParameter.PRINT_REQUEST_ATTRIBUTE_SET, printRequestAttributeSet);
			exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.FALSE);
			exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, Boolean.FALSE);
					
			exporter.exportReport();
			exporter = null;
			} else JOptionPane.showMessageDialog(null, "Eneboo Reports :: impresionDirecta :: No existe la impresora especificaca : ( " + impresora + " ).\n\nEspecifique alguna de las siguientes impresoras :\n" + listadoImpresorasDisponibles , "Eneboo Reports", 1);
						
		}catch (Exception e) {  
            JOptionPane.showMessageDialog(null, "impresionDirecta :: Se ha producido un error (Exception) : \n " + e.toString(), "Eneboo Reports", 1);
	    e.printStackTrace();
		       }  									
		       
		  }
		  
	public static void crearLogError(Exception error) {
		try
		{
			String cabecera;
			long tiempo = System.currentTimeMillis();
			String ficheroError = enebooreports.ficheroTemp+"_"+ tiempo + "_error.txt";
			
			///Generamos cabecera
			cabecera = "\nEneboo Reports :\n\n* " + enebooreports.build + ".\n* Versión Jasper Reports " + enebooreports.versionJR + ".\n* Nombre fichero " + enebooreports.ficheroTemp + ".\n\n\n* StackTrace Java :\n";  	

	
			
			FileOutputStream fos = new FileOutputStream(ficheroError);
			PrintStream ps = new PrintStream(fos);
			ps.printf(cabecera);
			error.printStackTrace(ps);
			ps.close();  
			JOptionPane.showMessageDialog(null, "Se ha producido un error : \n" + error.toString() + "\n\nConsulte " + ficheroError + " para más información \n ", "Eneboo Reports", 1);

	
	}catch (Exception e) {  
            JOptionPane.showMessageDialog(null, "crearLogError :: Se ha producido un error (Exception) : \n " + e.toString(), "Eneboo Reports", 1);
	    e.printStackTrace();
			     }
	System.exit(1);  		
	}

}
