package com.jemstep.consumer

import com.jemstep.consumer.Goal.BusinessModel
import java.io.BufferedWriter
import java.io.FileWriter
import java.io.IOException


object Object_Writer{

def writetofile(schemaName:String,OrganizationId:String, content: ListOfBussinesModel):Unit ={
  val  file="C:/Users/vanishree.y/sample_project/"+schemaName+"_"+OrganizationId+".txt"
  val fw = new FileWriter(file, true) ;
  fw.write(content.toString) ;
  fw.close()

  }
}


