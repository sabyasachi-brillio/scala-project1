package com.jemstep.model

import scala.beans.BeanProperty//, BooleanBeanProperty}

//remove if not needed
//import scala.collection.JavaConversions._

class CreateJobRequest(@BeanProperty val `object`: String,
                       @BeanProperty val contentType: String,
                       @BeanProperty val operation: String,
                       @BeanProperty val lineEnding: String)
