# language: en
# encoding: utf-8

@ATS @settings @authentication @smoke @critical
Feature: ATS Authentication
  """
  Tests authentication functionality for ATS (Applicant Tracking System)
  """

  Scenario: Login to ATS Dashboard
    Given Login to system with "https://teamso.com/dashboard/account/login" and "kadir.mogul@teamso.com" and "prodkadir" and "0" and "Akm@2026"
    Then verify login successful
    And Select menu "Aday Takip Sistemi" and sub-menu "5"
    And verify page opened successfully
