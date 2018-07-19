package com.jemstep.model

import com.jemstep.model.BusinessEntityModel._

object PortfolioByBrokerModel {

  /**
    *
    * Type One
    *
    */
  case class Orion(accountId: String)

  case class MI(currentLastUpdated: String)

  case class Pershing(mi: MI)

  case class AccountIntegrations(orion: Orion, pershing: Pershing)

  case class Positions(assetClass: String, costBasis: String,
                       description: String, unitPrice: String,
                       units: String, ticker: String, uuid: String)

  case class Accounts(accountType: String, accountNumber: String,
                      accountName: String, accountStatus: String,
                      dollarValue: String, update: String,
                      uuid: String, accountIntegrations: AccountIntegrations,
                      positions: List[Positions])

  case class Broker(accounts: List[Accounts], uuid: String)

  case class PortfolioByBroker(brokers: List[Broker]) extends BusinessModel {

    import EntityType._

    def extractEntityModels(userId: String, org: String, operation: String): List[EntityModel] = {

      val listOfAccountPe: List[AccountPe] =
        brokers.flatMap(x =>
          x.accounts.map(y => {
            AccountPe(
              Account_Name__c = y.accountName,
              Account_Number__c = y.accountNumber,
              Account_Status__c = y.accountStatus,
              Account_Type__c = y.accountType,
              Account_Value__c = y.dollarValue,
              Contact__c = userId,
              Date_Updated__c = y.update,
              Institution__c = x.uuid,
              Parent_Jemstep_Id__c = userId,
              Jemstep_Id__c = y.uuid)
          }))

      val listOfEntityModel1: EntityModel = EntityModel(ACCOUNT_PE, org, listOfAccountPe)


      /*val listOfHoldingPe: List[HoldingPe] = brokers.flatMap(x =>
        x.accounts.map(y => {
          HoldingPe(Account_Id__c = y.accountIntegrations.orion.accountId, Asset_Class__c = y.positions.assetClass,
            Cost_Basis__c = y.positions.costBasis, Date_Updated__c = y.update,
            Description__c = y.positions.description, Parent_Jemstep_Id__c = y.uuid,
            Jemstep_Id__c = y.positions.uuid, Price__c = y.positions.unitPrice,
            Quantity__c = y.positions.units, Symbol__c = y.positions.ticker, Value__c = y.dollarValue)
        })
      )*/

      val listOfHoldingPe: List[HoldingPe] = brokers.flatMap(x =>
        x.accounts.map(y => {
          y.positions.map(p =>
            HoldingPe(Account_Id__c = y.accountIntegrations.orion.accountId, Asset_Class__c = p.assetClass,
              Cost_Basis__c = p.costBasis, Date_Updated__c = y.update,
              Description__c = p.description, Parent_Jemstep_Id__c = y.uuid,
              Jemstep_Id__c = p.uuid, Price__c = p.unitPrice,
              Quantity__c = p.units, Symbol__c = p.ticker, Value__c = y.dollarValue))
        }).flatMap(x => x.map(y => y))
      )


      val listOfEntityModel2: EntityModel = EntityModel(HOLDING_PE, org, listOfHoldingPe)

      val listOfRtqPortfolio: List[RtqPe] = brokers.flatMap(x =>
        x.accounts.map(y => {
          RtqPeP(Jemstep_Id__c = userId,
            Portfolio_Value__c = y.dollarValue)
        })
      )

      val listOfEntityModel3: EntityModel = EntityModel(RTQ_PE_P, org, listOfRtqPortfolio)

      List(listOfEntityModel1, listOfEntityModel2, listOfEntityModel3)
    }
  }

}
