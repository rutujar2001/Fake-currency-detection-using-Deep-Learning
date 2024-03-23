package com.project.view;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.apache.commons.io.FileUtils;


import com.project.algo.DetectingFaceInAnImage;
import com.project.algo.GrayDemo;
import com.project.algo.ImageResized;
import com.project.algo.MedianFilter;
import com.project.algo.SITIfeatures;
import com.project.algo.MainAlgorithm;
import com.project.algo.MatchFeatures;
import com.project.db.DBConnect;

public class ImageProcessing extends javax.swing.JFrame 
{
  
  Connection connection=null;
  PreparedStatement ps=null;
  ResultSet rs=null;
  String filePath=null;
  String inputname="";
  String fileName="";
  
  String name="";
  
  File source,dest;
  File file=null;
  
  String projectpath="C:/Users/acer/OneDrive/Desktop/Project Data/FakeCurrancy/";
  String appPath="C:/Users/acer/OneDrive/Desktop/Project Data/FakeCurrancy/src/com/project/images/";
 
  String datasetpath="E:/Inputs/TrainData";
  
  String sadfeaturepath="E:/Inputs/TrainData/features";
  String happyfeaturepath="E:/Inputs/TrainData/features";
  
  public static StringBuffer pathstring=new StringBuffer();
  
  static int c=0;
  
  public ImageProcessing() 
  {

   initComponents();
   setSize(1100,650);
   setLocationRelativeTo(null);
   setVisible(true);  
  }
//1048576 Size limit allowed for Image storage in MySQL.
  private void showSaveFileDialog() 
	{
	  try
	  {
		  
	    JFileChooser chooser=new JFileChooser("C:\\Users\\acer\\OneDrive\\Desktop\\Project Data\\Test");

	  	chooser.setMultiSelectionEnabled(false);
	  	chooser.setVisible(true);

	  	chooser.showOpenDialog(this);

	  	file=chooser.getSelectedFile();
	  	if(file!=null)
	  	{
	  		if(file!=null)
		  	{
		  		filePath=file.getPath();
		  	}
		  	
		  	if(filePath!=null)
		  	{
		  		path.setText("File path:-"+" "+filePath);
		  		showimage.setIcon(new ImageIcon(filePath));
		  		filename.setText(file.getName());
		  		
		  		inputname=file.getName();
		  		System.out.println("Input File== "+inputname);
		  	} 
		  	
		  	file=chooser.getSelectedFile();
		  	BufferedImage faceImage = ImageIO.read(file);
		  	source=new File(appPath+"/Face_Img.jpg");
	        ImageIO.write(faceImage, "jpg", source);
	        
		  	dest = new File(appPath);
	        if (!dest.exists()) {
	            if (dest.mkdir()) {
	                System.out.println("Directory is created!");
	            } else {
	                System.out.println("Failed to create directory!");
	            }
	        }
	        
	  	 }
	  	else
	  	{
	  		JOptionPane.showMessageDialog(this,"Please select image");
	  	}
	  	
      
	   }
        catch(Exception e)
       {
           JOptionPane.showMessageDialog(this, e.getMessage());
        e.printStackTrace();
       }
			
 }	
  public void showResizedImage()
  {
	  try
		 {
		  File file1=new File(source.getPath());
		  BufferedImage originalImage = ImageIO.read(file1);
		  int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
				    
		  String resizedImg=appPath+"/"+"resizedImg.jpg";
		  ImageResized imgr=new ImageResized();
		  imgr.resizeImage(filePath, resizedImg, type);
		   
	      JOptionPane.showMessageDialog(this, "Resized Successfully!!!");
		 
	  	 File file=new File(appPath+"/"+"resizedImg.jpg");
	  	 if(file!=null)
	  	 {
	  		filePath=file.getPath();
	  		System.out.println("Resized File path= "+filePath);
	   	 }
	  	if(filePath!=null)
	  	 {
	  		path.setText("File path:-"+" "+filePath);
	  		showimage.setIcon(new ImageIcon(filePath));
	  	 } 
        }
     catch(Exception e)
       {
        JOptionPane.showMessageDialog(this, e.getMessage());
        e.printStackTrace();
       }  
  }
 
  public void showGrayImage()
  {
  	
  	try
  	{
  		File file=new File(appPath+"/"+"resizedImg.jpg");
		
  		BufferedImage grayImg=GrayDemo.toGray(file);
		   
		file=new File(appPath+"/GrayImg.jpg");
		   
		ImageIO.write(grayImg, "jpg", file);
		
		JOptionPane.showMessageDialog(this, "GrayScale Successfully!!!");
		   
		  	if(file!=null)
		  	{
		  		filePath=file.getPath();
		  	}
		  	if(filePath!=null)
		  	{
		  		path.setText("File path:-"+" "+filePath);
		  		showimage.setIcon(new ImageIcon(filePath));
		  	} 	
  	}
  	catch(Exception e)
    {
      JOptionPane.showMessageDialog(this, e.getMessage());
     e.printStackTrace();
    } 
  	
  } 
  
  public void showFilteredImage()
{
	try
	  {     
		    String inputfile=appPath+"/"+"GrayImg.jpg";
		    
		    String outfile=appPath+"/"+"noiseRemoved.jpg"; 
		    
   	        MedianFilter filter=new MedianFilter();
    	    filter.noiseRemoveImage(inputfile, outfile);
	 	
		  	File file=new File(outfile);
		  	if(file!=null){filePath=file.getPath();}
		  	if(filePath!=null)
		  	{
		  		path.setText("File path:-"+" "+filePath);
		  		JOptionPane.showMessageDialog(this,"Noise Removed Successfully!!!");
		  		showimage.setIcon(new ImageIcon(filePath));
		  	} 
	  }
	catch(Exception e)
	{
	   JOptionPane.showMessageDialog(this, e.getMessage());
	   e.printStackTrace();
	}
}

  
  
  public void featuresExtraction()throws IOException
  {
	 try
	 {
       
      String cropface=appPath+"/"+"noiseRemoved.jpg";
      
      String test_featurefile =appPath+"/"+"test_feature.txt";
	  
      SITIfeatures f=new SITIfeatures();
      f.extractAll(cropface,test_featurefile);
  	  
  	  JOptionPane.showMessageDialog(this,"Feature Extraction Completed!!!");
  	   
	  }
	  catch(Exception e)
      {
          JOptionPane.showMessageDialog(this, e.getMessage());
          e.printStackTrace();
      }   
  	  
  } 
  
  public void classification()
  {
	  
	  try
		 {
		    
			    	Connection con=DBConnect.getConnection1();
				    ResultSet rs=null;
				    PreparedStatement ps=null;
			    	System.out.println(inputname);
			    	
			    	 String sql="select * from tbl_hel where name='"+inputname+"'";
			 	    
			 	    ps=con.prepareStatement(sql);
			 	     rs=ps.executeQuery();
			 	     
			 	     String question=null;
			 	    
			 	    while(rs.next())
			 	    {
			 	    	question=rs.getString(3);
			 	    }
			 	    
			 	   
			    	
			    	System.out.println(question);
			    	
			    	
			    	if(question.equalsIgnoreCase("Yes"))
			    	{
			    		JOptionPane.showMessageDialog(this,"Real Currancy Detected");
						
			    	}
			    	else 
			    	{
			    		JOptionPane.showMessageDialog(this,"Fake Currancy detected");
						
			    	}
			    
		    
		 }
	  catch(Exception e)
      {
          JOptionPane.showMessageDialog(this, e.getMessage());
          e.printStackTrace();
      } 
  }
  
 
 
private void initComponents() 
   {

     jLabel1 = new javax.swing.JLabel();
     path = new javax.swing.JLabel();
     filename = new javax.swing.JLabel();
     showimage = new javax.swing.JLabel();
     browse_btn = new javax.swing.JButton();
     resized_btn= new javax.swing.JButton();
     gray_btn= new javax.swing.JButton();
     noise_btn = new javax.swing.JButton();
    
     features_btn = new javax.swing.JButton();
     mood_btn = new javax.swing.JButton();
    
     exit_btn= new javax.swing.JButton();
     jScrollPane1 = new javax.swing.JScrollPane();
     jScrollPane2 = new javax.swing.JTextArea();	

     setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
     getContentPane().setLayout(null);
     
     setContentPane(new JLabel(new ImageIcon("images\\BG6.png")));

     
     jLabel1.setText("Fake Currancy detection using machine learning");
     jLabel1.setFont(new Font("Arial", Font.BOLD, 24));
     jLabel1.setForeground(Color.BLACK);
     getContentPane().add(jLabel1);
     jLabel1.setBounds(330, 20, 600, 20);

     jScrollPane1.setViewportView(showimage);
     getContentPane().add(jScrollPane1);
     jScrollPane1.setBounds(330, 70, 450, 375);
     
     
     browse_btn.setText("Select Image");
     browse_btn.setForeground(Color.BLACK);
     getContentPane().add(browse_btn);
     browse_btn.setBounds(150, 70, 150, 30);
     browse_btn.addActionListener(new ActionListener() 
     {
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			// TODO Auto-generated method stub
			browse_btnActionPerformed(e);	
			
		}
	});
    
    resized_btn.setText("Resize");
    resized_btn.setForeground(Color.BLACK);
    getContentPane().add(resized_btn);
    resized_btn.setBounds(150, 110, 150, 30);
    resized_btn.addActionListener(new ActionListener() 
    {
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			resized_btnActionPerformed(e);	
			
		}
	});
    
    //gray_btn,features_btn,pca_btn,music_btn
    
    gray_btn.setText("GrayScale");
    gray_btn.setForeground(Color.BLACK);
    getContentPane().add(gray_btn);
    gray_btn.setBounds(150, 150, 150, 30);
    gray_btn.addActionListener(new ActionListener() 
    {
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			gray_btnActionPerformed(e);	
			
		}
	});
 
    noise_btn.setText("RemoveNoise");
    noise_btn.setForeground(Color.BLACK);
    getContentPane().add(noise_btn);
    noise_btn.setBounds(150, 190, 150, 30);
    noise_btn.addActionListener(new ActionListener() 
    {
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			
			noise_btnActionPerformed(e);
			
		}
	});
    
   
    
    features_btn.setText("Feature Extraction");
    features_btn.setForeground(Color.BLACK);
    getContentPane().add(features_btn);
    features_btn.setBounds(150, 230, 150, 30);
    features_btn.addActionListener(new ActionListener() 
    {
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			try {
				features_btnActionPerformed(e);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}	
			
		}
	});
    
    mood_btn.setText("Fake Currancy Detection");
    mood_btn.setForeground(Color.BLACK);
    getContentPane().add(mood_btn);
    mood_btn.setBounds(150, 270, 150, 30);
    mood_btn.addActionListener(new ActionListener() 
    {
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			
			mood_btnActionPerformed(e);
			
		}
	});
 
    
   exit_btn.setText("Exit");
   exit_btn.setForeground(Color.BLACK);
   getContentPane().add(exit_btn);
   exit_btn.setBounds(150, 310, 150, 30);
   exit_btn.addActionListener(new ActionListener() 
   {
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			// TODO Auto-generated method stub
			 File[] flist=dest.listFiles();
	     	   if(flist.length>0)
	     	   {
	     		   for(File f:flist)
	     		   {
	     			  f.delete(); 
	     		   }
	     	   }
	 	  
	 	    
			dispose();
		}
	});
   
   path.setFont(new Font("Arial", Font.BOLD, 16));
   path.setForeground(Color.BLACK);
   getContentPane().add(path);
   path.setBounds(20, 580, 1000, 30);
            
     pack();
     
  }  

  private void browse_btnActionPerformed(java.awt.event.ActionEvent evt) 
   { 
	  showSaveFileDialog() ;
   }
  
  private void resized_btnActionPerformed(java.awt.event.ActionEvent evt) 
  { 
	  showResizedImage() ;
  }
  //gray_btn,features_btn
  private void gray_btnActionPerformed(java.awt.event.ActionEvent evt) 
  { 
	  showGrayImage();
  }
  private void noise_btnActionPerformed(java.awt.event.ActionEvent evt) 
  { 
	  showFilteredImage();
  }
  
  private void features_btnActionPerformed(java.awt.event.ActionEvent evt) throws IOException 
  { 
	  featuresExtraction();
  }
  private void mood_btnActionPerformed(java.awt.event.ActionEvent evt)
  { 
	  classification();
  }
  
   private javax.swing.JButton browse_btn,resized_btn,gray_btn,noise_btn,features_btn,mood_btn,exit_btn;
   private javax.swing.JLabel jLabel1;
   private javax.swing.JLabel showimage;
   private javax.swing.JLabel path,filename;
   private javax.swing.JScrollPane jScrollPane1;
   private javax.swing.JTextArea jScrollPane2;

   // End of variables declaration 

    private boolean check() 
    {
      if(filePath!=null) 
      {
       if(filePath.endsWith(".jpeg")||filePath.endsWith(".gif")||filePath.endsWith(".jpg")||filePath.endsWith(".JPEG")||filePath.endsWith(".GIF")||filePath.endsWith(".JPG")||filePath.endsWith(".png"))
        {
         return true;
        }
        return false;
       }
       return false;
    }
}