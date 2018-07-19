package com.jemstep.model

import com.jemstep.model.BusinessEntityModel.EntityModel

trait BusinessModel {

  def extractEntityModels(userId: String, org: String, operation: String): List[EntityModel]

}