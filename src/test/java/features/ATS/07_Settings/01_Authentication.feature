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
    And perform logout from system

    Examples:
      | pageUrl                                    | email                    | searchText | accountIndex | password |
      | https://teamso.com/dashboard/account/login | kadir.mogul@teamso.com  | prodkadir  | 0            | Akm@2026  |
      | https://teamso.com/dashboard/account/login | kadir.mogul@teamso.com  | prodkadir  | 1            | Akm@2026  |


 
