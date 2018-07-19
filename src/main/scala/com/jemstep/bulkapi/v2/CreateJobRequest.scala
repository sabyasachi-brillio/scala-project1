package com.jemstep.bulkapi.v2

import scala.beans.{BeanProperty}

class CreateJobRequest(@BeanProperty val `object`: String,
                       @BeanProperty val contentType: String,
                       @BeanProperty val operation: String,
                       @BeanProperty val lineEnding: String)
