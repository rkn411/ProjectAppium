Feature: Log in to the app

  Background:
    Given  User navigates to login screen

	
@login 
Scenario Outline: Valid login for the app for the first time 

	Given User enter the "<username>" and "<password>" 
	And App permission screen should be displayed 
	When user enter the tac in the Tac Screen for "<username>" 
	Then user able to see the account summary screen 
	
	Examples: 
		|username             | password  |
		|inbranchtest004      | Welcome1 |
    
@loginInvalid 
Scenario Outline: Invalid login verify for the app 

	Given User enter the "<username>" and "<password>" 
	Then  user should see the error message display 
	
	Examples: 
		|username             | password  |
		|inbranchtest	      | Welcome1 |