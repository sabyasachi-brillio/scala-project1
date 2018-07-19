package com.jemstep.model

import com.jemstep.model.BusinessEntityModel.{EntityModel, EntityType, RtqPe, RtqPeU}

object UserIdentifiedModel {

  /**
    *
    * Type Four
    *
    */
  case class AccessMeta(clientAddr: String)

  case class UserIdentified(accessMeta: AccessMeta,
                            emailAddress: String,
                            lastName: String, firstName: String) extends BusinessModel {

    import EntityType._

    def extractEntityModels(userId: String, org: String, operation: String): List[EntityModel] = {

      val rtqPe: RtqPe =
        RtqPeU(Jemstep_Id__c = userId,
          Address__c = accessMeta.clientAddr, Email__c = emailAddress, Last_Name__c = lastName, First_Name__c = firstName)

      EntityModel(RTQ_PE_U, org, rtqPe :: Nil) :: Nil
    }
  }

}
