package pdf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;

import env.Enviroment;
import filecopy.FileListing;



public class PDFUnlock {


		public class PDFFileObj {
 			Boolean success;
 			File file;
 			String filePath;
 			
			public PDFFileObj(boolean success, File file, String filePath) {
				super();
				this.success = success;
				this.file = file;
				this.filePath = filePath;
			}
			public Boolean getSuccess() {
				return success;
			}
			public void setSuccess(Boolean success) {
				this.success = success;
			}
			public File getFile() {
				return file;
			}
			public void setFile(File file) {
				this.file = file;
			}
			public String getFilePath() {
				return filePath;
			}
			public void setFilePath(String filePath) {
				this.filePath = filePath;
			}
 			
 			
 		}
	
	
	public static String getStringBetween1(String line,String first,String second){
		String between = getStringBetween(line,"(.*?)"+first+"(.*?)"+second+"(.*?)");
		return between;
	}
	
	public static String getStringBetween(String line,String first,String second){
		String between = getStringBetween(line,first+"(.*?)"+second);
		return between;
	}

	public static String getStringBetween(String line,String pattern){
		Pattern p;
		p = Pattern.compile(pattern);
		Matcher m = p.matcher(line);
		if (m.find()){
			//System.out.println("m.group(0)="+m.group(0));
			//System.out.println("m.group(1)="+m.group(1));
			//System.out.println("m.group(2)="+m.group(2));
			return m.group(2);
		}
		return null;
	}
	
	/**
	 * @param args
	 * @throws ParseException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws ParseException, IOException {
		// TODO Auto-generated method stub
		String[] suffixAllowed = {"pdf","PDF"} ;
		String[] excludeDirOrFile = {"OLD","NEW","additional_hours"};
		String[] includeDirOrFile = {}; //if not exists, ignore
//		String[] srcDir = {"C:\\shai\\Dropbox\\x1\\polisa\\clal\\"};
//		String[] password = {"6841"};
		
		String[] srcDir = new String[1];
		if (args[0] != null){
			srcDir[0] = Enviroment.getDropboxHome()+"\\"+args[0];
		}else{
			srcDir[0] = Enviroment.getDropboxHome()+"\\x1\\payment\\salaries\\shai\\logon-2014\\2019";
		}
		String[] password = new String[1];
		if (args[1] !=null){
			password[0] = args[1];
		}else{
			password[0] = "yuval";
		}

//		String[] srcDir = {"C:\\shai\\Dropbox\\x1\\polisa\\meitav"};
//		String[] password = {"1107215"};
		
		for (int i = 0; i < srcDir.length; i++) {
			handlePDF(suffixAllowed, excludeDirOrFile, includeDirOrFile, password[i], srcDir[i]);			
		}

	      System.out.println("finish...");
	    
		
	}

	private static void handlePDF(String[] suffixAllowed,
			String[] excludeDirOrFile, String[] includeDirOrFile, String password,
			String srcDir) throws IOException {
		
		//String srcDir = "C:\\mega";
	    File startingDirectory= new File(srcDir);
	    List<File> files = FileListing.getFileListing(startingDirectory);
	    HashMap<String, List<File>> FilesByDateMap = new HashMap<String, List<File>>();
	    //print out all file names, in the the order of File.compareTo()
	    List newDirectoyList = null;
	    String suffixAllowedStringRegExp = "\\.(" + StringUtils.join(suffixAllowed,"|")+")";
		SimpleDateFormat sdfWA = new SimpleDateFormat("yyyyMMdd");
		System.out.println("Handle File:");
	    for(File file : files ){
	    	if (! isfileAllowed (suffixAllowed,file,excludeDirOrFile,includeDirOrFile))  continue;
	    	if (! isFileEncrypted(file)) continue;
	    	PDFUnlock.PDFFileObj p = pdfFile(file,password);
	    	exchangeFile(file ,p.getFilePath() );
	    	System.out.println(file.getPath());
	    }
	}
	
	 private static boolean isFileEncrypted(File file) {
		 try
		 {
		     PDDocument document = PDDocument.load(file);

		     if(document.isEncrypted())
		     {
		       return true;
		     }
		 }catch(IOException ioe){
			 
		 }
		 return false;
	 }

	private static boolean createDir(String targetDir) {
			boolean success = (new File(targetDir)).mkdir();
		    if (success) {
		      System.out.println("Directory: " + targetDir + " created");
		      return true;
		    } 
		    return false;
	}

	private static boolean isfileAllowed(String[] suffixAllowed, File file,String[] excludeDirOrFile,String[] includeDir) {
		 String fileName = file.getName();
		 String fileFullName = file.getPath();
		 //List<String> excludeDirList = Arrays.asList(excludeDir);
		 for (String excludeDirStr : excludeDirOrFile) {
			 if (fileFullName.matches(".*"+excludeDirStr+".*")){
				 return false;
			 }
		 }
		 
		 for (String suffixAllowedStr : suffixAllowed) {
			 if (fileFullName.endsWith(suffixAllowedStr) && isExistsDir(includeDir,fileFullName)){
				 	return true;
			 }
		}
		return false;
	}
	 
	 
	 private static boolean isfileWhatsApp( File file) {
		 String fileName = file.getName();
		 if (fileName.matches("IMG-"+".*"+"WA"+".*")){
			 return true;
		 }
		 return false;
	 }	 

	private static boolean isExistsDir(String[] includeDir, String fileFullName) {
		if  (ArrayUtils.isEmpty(includeDir)) 
			return true;
		for (String includeDirStr : includeDir) {
			if (fileFullName.matches(".*"+includeDirStr+".*")){
				return true;
			}
		}
		return false;
	}

	private static void copyfile(String srFile, String dtFile){
		    try{
		      File f1 = new File(srFile);
		      File f2 = new File(dtFile);
		      InputStream in = new FileInputStream(f1);
		      
		      //For Append the file.
//		      OutputStream out = new FileOutputStream(f2,true);

		      //For Overwrite the file.
		      OutputStream out = new FileOutputStream(f2);

		      byte[] buf = new byte[1024];
		      int len;
		      while ((len = in.read(buf)) > 0){
		        out.write(buf, 0, len);
		      }
		      in.close();
		      out.close();
		      System.out.println("File: " +dtFile+" copied.");
		    }
		    catch(FileNotFoundException ex){
		      System.out.println(ex.getMessage() + " in the specified directory.");
		      System.exit(0);
		    }
		    catch(IOException e){
		      System.out.println(e.getMessage());      
		    }
		  }

	 private static void exchangeFile(File srFile, String afterPDFFile){
		 
	    	String srcDir=srFile.getParent();
	    	String srcFileName = srFile.getName();
	    	String fileNameSrcTarget = srcDir + "//" +  FilenameUtils.removeExtension(srcFileName) +"_OLD." + FilenameUtils.getExtension(srcFileName) ;
	    	File fileSerTarget = new File(fileNameSrcTarget);
	    	File trgFile = new File(afterPDFFile);
	    	File newFile = new File(srFile.getPath());
	    	try {
				FileUtils.copyFile(srFile,fileSerTarget); //copy the old file as prev
				FileUtils.forceDelete(srFile); //delete
				FileUtils.copyFile(trgFile,newFile); //copy the after pdf file
				FileUtils.forceDelete(trgFile); //delete
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
		   }
	
	
	 private static void moveFile(String srFile, String dtFile){
		    try{
		    	
		    			    	
		        // File (or directory) to be moved
		        File file = new File(srFile);
		        
		        // Destination directory
		        File dir = new File(dtFile);

		        Runtime rt = Runtime.getRuntime();
		        
				String[] command =  new String[6];
		        command[0] = "cmd";
		        command[1] = "/C";
		        command[2] = "move";
		        command[3] = "/Y";
		        command[4] = srFile;
		        command[5] = dtFile;
		          
		 		        
	        	//String s = "cmd.exe move /Y "  + srFile + " " + dtFile;
	        	//System.out.println(s);
		        	rt.exec(command);
		        // Move file to new directory
//		        boolean success = file.renameTo(new File(dir, file.getName()));
//		        if (!success) {
//				      System.out.println("rename " + file.getName() +  "failed in the specified directory.");
//		        }else{
//				      System.out.println("rename " + file.getName() +  "in the specified directory passed successfully");
//		        }
		        
		    }catch(Exception e){
		      System.out.println(e.getMessage());      
		    }
		  }
	 
	 
	 
	 private static PDFFileObj pdfFile(File file, String password){
		    
		 String fileNameTarget = null;
		 try{
		    	

		    	String dir=file.getParent();
		    	String fileName = file.getName();
		    	fileNameTarget = dir + "/" +  FilenameUtils.removeExtension(fileName) +"_NEW." + FilenameUtils.getExtension(fileName) ;
		        // Destination directory
		        Runtime rt = Runtime.getRuntime();
		        
				String[] command =  new String[7];
				int i=0;
		        command[0] = "cmd"; i++;
		        command[i] = "/C"; i++;
		        command[i] = "qpdf"; i++;
		        command[i] = "--decrypt"; i++;
		        if (password!=null){
			        command[i] = "--password="+password; i++;
		        }
		        command[i] = file.getPath(); i++;
		        command[i] = fileNameTarget;
		          
		 		        
		        Process proc = rt.exec(command);
		        //String comm="qpdf --decrypt --password="+password + " " + file.getPath() +" "+ fileNameTarget;
		        //Process proc = rt.exec(comm);
				BufferedReader stdInput = new BufferedReader(new InputStreamReader(
						proc.getInputStream()));
	
				BufferedReader stdError = new BufferedReader(new InputStreamReader(
						proc.getErrorStream()));
	
				// read the output from the command
				StringBuilder sbOut = new StringBuilder();
				StringBuilder sbErr = new StringBuilder();
				boolean success = true;
				String s=null;
				while ((s = stdInput.readLine()) != null) {
					sbOut.append(s);
				}

				while ((s = stdError.readLine()) != null) {
					sbErr.append(s);
				}
				String[] errorStringArr = {"error","invalid"};
				for (String errorStr: errorStringArr){
					String matchStr = ".*"+errorStr+".*";
					if (sbOut.toString().matches(matchStr) || sbErr.toString().matches(matchStr)){
						success = false;
						break;
					}
				}
					
		        
		    }catch(Exception e){
		      System.out.println(e.getMessage());      
		    }
		 	PDFUnlock.PDFFileObj p = new PDFUnlock().new PDFFileObj(true,null,fileNameTarget); 
			return p;
		  }


	 
}
