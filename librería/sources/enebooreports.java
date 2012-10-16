import java.io.*; //include Java's standard Input and Output routines
import java.sql.*;
import java.util.*;
import javax.swing.*;
import java.lang.ClassLoader;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.PrinterJob;
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

public class enebooreports {
 
                public static void main(String[] args) throws IOException {
			try {
                            
                            String ficheroTemp, impresora;
                            String changelog = "";
			    String build = "Build 20121016 ( Lola )";
			    Boolean pdf,impDirecta, guardaTemporal;
			    int nCopias, nParametrosJasper;
			    long start;
			    Class.forName(args[0]);
			    //JOptionPane.showMessageDialog(null, "Init iniciado" , "Eneboo Reports", 1);
			    InputStream is=enebooreports.class.getClass().getResourceAsStream("/otros/init.jasper");
			    JasperReport creditos = (JasperReport) JRLoader.loadObject(is);
			    JasperReport report = creditos;      
                            Connection conn = DriverManager.getConnection(args[1],args[2],args[3]);
                            //JOptionPane.showMessageDialog(null, "Init finalizado" , "Eneboo Reports", 1);
			    do
                              {
                            BufferedReader stdin = new BufferedReader (new InputStreamReader(System.in));
			    System.out.flush();// empties buffer, before you input text
			    ficheroTemp =""; //Nombre fichero Temporal
          		    ficheroTemp = stdin.readLine();
			   start = System.currentTimeMillis(); /* Para controlar el tiempo */					
                            guardaTemporal = false; //bool que indica si se borra o no el temp al finalizar de usarlo.
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
						guardaTemporal = true;
						hm.put("VERSION", build);
     						InputStream chl = enebooreports.class.getResourceAsStream("/otros/changelog");
     						BufferedReader br2 = new BufferedReader(new InputStreamReader(chl));
     						String line= null;
     						String listadoCompleto = "";
     						 while (null != (line = br2.readLine())) {
        								 listadoCompleto = listadoCompleto + line + "<br>";
      											}
						hm.put("CHANGELOG",listadoCompleto);
						}
					else {     
                          	       
                                       if (ficheroTemp.equals( "Repetir" ))//Solo compilar si no se llama repetir.
                                       			guardaTemporal = true;
                                        		 else 
                                        		 report = JasperCompileManager.compileReport(ficheroTemp);
                                        		 
                                        		

					
					   for(int j = 0; j < nParametrosJasper; j++ )
                         				  if(!parametroValor[j].equals( "") && !parametroNombre[j].equals( ""))
	 						 		hm.put(parametroNombre[j], parametroValor[j]); //Seteamos Parametros
						 }
						 
					JasperPrint print = JasperFillManager.fillReport(report, hm, conn); //Rellenamos el report compilado
					
					if (impDirecta) impresionDirecta( impresora, nCopias, print );
							  else
					 			if(pdf) 
					 				{
     						 			JasperExportManager.exportReportToPdfFile(print, impresora); // Exporta el informe a PDF
									}
								else mostrarVisor( print, build, start);
							  	       
				if (!guardaTemporal)
						{
                                   		File ficheroT = new File(ficheroTemp);
                                    		if (!ficheroT.delete())
                                         		JOptionPane.showMessageDialog(null, "El fichero Temporal " + ficheroTemp + " no se puede borrar." , "Eneboo Reports", 1);
						}
					  
					
 				} while (!ficheroTemp.equals( "" ));
                                        
						
			} catch (JRException e) {
				JOptionPane.showMessageDialog(null, "Se ha producido un error (JRException) : \n " + e.toString(), "Eneboo Reports", 1);
				e.printStackTrace();
			}catch (SQLException e) {
				JOptionPane.showMessageDialog(null, "Se ha producido un error (SQLException) : \n " + e.toString(), "Eneboo Reports", 1);
				 e.printStackTrace();
			}		
			 catch (ClassNotFoundException e) {
				JOptionPane.showMessageDialog(null, "Se ha producido un error (ClassNotFoundException) : \n " + e.toString(), "Eneboo Reports", 1);
				 e.printStackTrace();
			} 
			 catch (Exception e) {
			 if (!e.getMessage().equals("null"))  //Para que no muestre mensaje de error cuando se cierra el ejecutable 
            		JOptionPane.showMessageDialog(null, "main :: Se ha producido un error (Exception) : \n " + e.toString() , "Eneboo Reports", 1);
	    e.printStackTrace();
		       }  
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public static void mostrarVisor(JasperPrint print, String build, long start) 
									{
							try {
									JasperViewer viewer = new JasperViewer(print, false);
                                    					//viewer.setSize(850, 500);
									//viewer.setIconImage(new ImageIcon("elefante.gif").getImage()); //No lo coge
									viewer.setAlwaysOnTop(true); //Siempre delante    
									viewer.setTitle(print.getName() + " - Eneboo Reports"); 
									viewer.setVisible(true);
									viewer.setAlwaysOnTop(false); //Ahora no interesa estar siempre 
								    	}
					 catch (Exception e) {  
            JOptionPane.showMessageDialog(null, "mostrarVisor :: Se ha producido un error (Exception) : \n " + e.toString() , "Eneboo Reports", 1);
	    e.printStackTrace();
		       }
		       }  
	
	
				
					
					
	public static void impresionDirecta(String impresora, int nCopias, JasperPrint print) {
	
		try
		{
		
		//Aqui imprimimos directamente en var impresora...
		//JasperPrint print = JasperFillManager.fillReport( this.class.getResource("/classpath/yourReport.jasper").getPath(), new HashMap(), new yourReportDataSource());
		PrinterJob job = PrinterJob.getPrinterJob();
		/* Create an array of PrintServices */
		PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
		int selectedService = 0;
		/* Scan found services to see if anyone suits our needs */
		for(int i = 0; i < services.length;i++){
					if(services[i].getName().toUpperCase().contains(impresora)){
					/*If the service is named as what we are querying we select it */
					selectedService = i;
							}
		job.setPrintService(services[selectedService]);
		PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
		MediaSizeName mediaSizeName = MediaSize.findMedia(4,4,MediaPrintableArea.INCH);
		printRequestAttributeSet.add(mediaSizeName);
		printRequestAttributeSet.add(new Copies(nCopias)); // *************** Numero de copias
		JRPrintServiceExporter exporter;
		exporter = new JRPrintServiceExporter();
		exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
		/* We set the selected service and pass it as a paramenter */
		exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE, services[selectedService]);
		exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, services[selectedService].getAttributes());
		exporter.setParameter(JRPrintServiceExporterParameter.PRINT_REQUEST_ATTRIBUTE_SET, printRequestAttributeSet);
		exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.FALSE);
		exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, Boolean.TRUE);
		exporter.exportReport();
							} 
						
		}catch (Exception e) {  
            JOptionPane.showMessageDialog(null, "impresionDirecta :: Se ha producido un error (Exception) : \n " + e.toString(), "Eneboo Reports", 1);
	    e.printStackTrace();
		       }  									}	
}
