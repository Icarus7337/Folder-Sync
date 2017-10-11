package folderSync;

import java.util.ArrayList;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class Sync {

	public static void main(String[] args) throws Exception
	{
		File b = new File("////192.168.43.193");	//The complete paths of the folders
		File a = new File("C:\\Users\\Rahul\\Desktop\\A");			//are to be provided.

		syncFolders(a,b);
	}

	private static void syncFolders(File a, File b) throws Exception 
	{
		File afiles[] = a.listFiles();
		File bfiles[] = b.listFiles();
		
		if (a.isFile()||b.isFile())		//Error check: in case the address given points to a file instead of folder.
		{
			System.out.println("Folders needed for syncing");
			System.exit(0);
		}
		
		else if (afiles.length==0)		// If the folder is empty, copy corresponding files to it.
		{
			for (int i=0; i<bfiles.length; i++)
			{
				copyFiles(a, bfiles[i]);
			}
		}
		
		else if (bfiles.length==0)		// If the folder is empty, copy corresponding files to it.
		{
			for (int i=0; i<afiles.length; i++)
			{
				copyFiles(b,afiles[i]);
			}
		}
		
		else
		{
			
			boolean arra[] = new boolean[afiles.length];	//Auxiliary boolean arrays to store
			boolean arrb[] = new boolean[bfiles.length];	//if files have been synced or not.
			
			for (int i=0; i<afiles.length; i++)		//To sync same named files/sub-folders in different folders.
			{
				for(int j=0; j<bfiles.length; j++)
				{
					if(afiles[i].getName().compareTo(bfiles[j].getName())==0)
					{
						if(afiles[i].isFile()&&bfiles[j].isFile()&&afiles[i].lastModified()!=bfiles[j].lastModified())		//Names are same and last modified fields are not same (this means they haven't been synced)
						{
							arra[i]=true;
							arrb[j]=true;
							syncFiles(afiles[i], bfiles[j]);
						}
						
						if (afiles[i].isDirectory()&&bfiles[j].isDirectory())		//Sync sub-folders with same name.
						{
							syncFolders(afiles[i], bfiles[j]);
							arra[i]=true;
							arrb[j]=true;
						}
					}
						
				}
			}
			
			for (int i=0; i<afiles.length; i++)		//To copy files/folders from A folder that are missing in folder B.
			{
				if (arra[i]==false)
				{
					copyFiles(b,afiles[i]);				
					arra[i] = true;
				}
			}
			System.out.println();
			for (int i=0; i<bfiles.length; i++)		//To copy files/folders from B folder that are missing in folder A.
			{
				if (arrb[i]==false)
				{
					copyFiles(a,bfiles[i]);
					arrb[i] = true;
				}
			}
		}
		
	}

	private static void syncFiles(File a, File b) throws Exception		//Sync same files with same names.
	{
		RandomAccessFile af = new RandomAccessFile(a, "rw");
		RandomAccessFile bf = new RandomAccessFile(b, "rw");
		
		long timeelapsed = a.lastModified()-b.lastModified();
		
		String ainp = new String();
		String binp = new String();
		
		if (a.lastModified()==b.lastModified())		//Files are already in sync
		{
			b.setLastModified(System.currentTimeMillis());
			a.setLastModified(b.lastModified());
			af.close();
			bf.close();
		}
		
		else if (Math.abs(timeelapsed)<=300000)		//If they have been modified within 5 minutes of each other but not synced both users
		{											//changed them independently on different folders and both sets of data need to be preserved.
			
			List<String> l = new ArrayList<String>();
			
			while(true)
			{
				
				ainp = af.readLine();
				binp = bf.readLine();
				if (ainp!=null && binp!=null)
				{
					l.add(ainp);
					
					if(ainp.compareTo(binp)!=0)
					{
						l.add(binp);
					}
					
				}
				
				else if (ainp==null && binp!=null)
				{
					l.add(binp);
				}
				
				else if (ainp!=null && binp==null)
				{
					l.add(ainp);
				}
				
				else
				{
					break;
				}
			}
			
			af.seek(0);
			bf.seek(0);

			for(int i=0; i<l.size(); i++)
			{
				af.writeBytes(l.get(i));
				af.writeBytes("\r\n");
				bf.writeBytes(l.get(i));
				bf.writeBytes("\r\n");
			}
		
			a.setLastModified(System.currentTimeMillis());
			b.setLastModified(a.lastModified());				//Set their last modified fields as same
			af.close();
			bf.close();
		}
		
		else
		{
			
			if (a.lastModified()>b.lastModified()) //A is most recent version 
			{
				bf.close();
				af.close();
				overwrite(b,a);
				
			}
			
			else //B is most recent version
			{
				bf.close();
				af.close();
				overwrite(a,b);
			
			}
		}
		
	}

	private static void overwrite(File old, File recent) throws Exception		//Overwrite old with recent.
	{
		Files.copy(recent.toPath(), old.toPath(), StandardCopyOption.REPLACE_EXISTING);
		old.setLastModified(recent.lastModified());		
	}
	
	private static void copyFiles(File dest, File src) throws Exception			//Copy from source to destination.
	{
		File tmp = new File(dest.getPath()+"\\"+src.getName());
		Files.copy(src.toPath(), tmp.toPath(), StandardCopyOption.REPLACE_EXISTING);
		tmp.setLastModified(src.lastModified());
		
		if (tmp.isDirectory())
		{
			syncFolders(tmp, src);
		}
	}
}
