# language: en
# encoding: utf-8

@ATS @settings @authentication @smoke @critical
Feature: ATS Authentication
  """
  Tests authentication functionality for ATS (Applicant Tracking System)
  """

  Scenario Outline: Login to ATS Dashboard
    Given perform login with "<pageUrl>" and "<email>" and "<searchText>" and "<accountIndex>" and "<password>"
    Then verify login successful
    And select menu "<menuName>"
    And select sub-menu index "<subMenuIndex>"
    And verify page opened successfully

    Examples:
      | pageUrl                                    | email                    | searchText | accountIndex | password | menuName          | subMenuIndex |
      | https://teamso.com/dashboard/account/login | kadir.mogul@teamso.com  | prodkadir  | 0            | Akm@2026  | Aday Takip Sistemi | 6            |




 
