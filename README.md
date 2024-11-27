models/
  user/
    User.java                 // Core user profile 
    UserDonation.java         // Donation record
    UserReservation.java      // Food reservation
    SavedRecipe.java          // Saved recipe reference
    
  foodbank/  
    FoodBank.java             // Food bank details
    FoodInventory.java        // Inventory tracking
    DailyPin.java            // PIN management
    
  recipe/
    Recipe.java               // Recipe details
    NutritionFacts.java       // Nutrition information
    
  menu/
    MenuRahmah.java          // Menu listing
    StallInfo.java           // Vendor details
    
  chat/
    ChatMessage.java         // Chat history

viewmodels/ (repository)
  MainViewModel.java
  

  auth/
    LoginViewModel.java
    RegisterViewModel.java
    
  donation/
    DonationViewModel.java 
    ReservationViewModel.java
    
  foodbank/
    FoodBankViewModel.java
    InventoryViewModel.java
    
  recipe/
    RecipeViewModel.java
    SavedRecipeViewModel.java
    
  menu/
    MenuRahmahViewModel.java

  leaderboard/
    LeaderboardViewModel.java
    
  chat/
    ChatViewModel.java

views/
  MainActivity.java

  auth/
    LoginActivity.java
    RegisterActivity.java
    ForgotPasswordActivity.java
    
  donation/
    DonationFragment.java
    DonationListFragment.java
    ReservationFragment.java
    
  foodbank/
    FoodBankFragment.java
    InventoryFragment.java
    PinVerificationDialogFragment.java
    
  recipe/
    RecipeListFragment.java 
    RecipeDetailFragment.java
    SavedRecipesFragment.java
    
  menu/
    MenuRahmahFragment.java
    MenuDetailFragment.java
    
  chat/
    ChatFragment.java

repositories/
  UserRepository.java         // User profile & auth 
  DonationRepository.java     // Food donations
  FoodBankRepository.java     // Food bank management  
  RecipeRepository.java       // Recipe data
  MenuRepository.java         // Menu Rahmah data
  ChatRepository.java         // Chat history

utils/
  FirebaseUtils.java          // Firebase helpers maybe in login
  ValidationUtils.java        // Input validation for the daily refreshed pin
  DateUtils.java             // Date formatting
  NotificationUtils.java      // Push notifications
  B40Utils.java              // B40 verification (Might no need to have)
  ImageUtils.java            // Image handling 

services/
  NotificationService.java    // Push notification handling
  ChatbotService.java        // AI chatbot integration
  PinService.java            // PIN management



(Login/Register put where?)
res/layout/
ctivity_main.xml                    # Main container with bottom nav
activity_splash.xml                  # Splash screen (might no need)

activity_login.xml              # Login screen
activity_register.xml           # Registration screen
activity_forgot_password.xml    # Password reset flow

fragment_home.xml               # Main home page
fragment_menu_list.xml          # Menu Rahmah list
fragment_menu_detail.xml        # Menu detail view

fragment_foodbank_list.xml      # Food bank listing
fragment_donation.xml           # Donation flow
fragment_reservation.xml        # Reservation flow
fragment_foodbank_detail.xml    # Food bank details

fragment_recipe_list.xml        # Recipe listing
fragment_recipe_detail.xml      # Recipe details
fragment_saved_recipes.xml      # Saved recipes

fragment_chat.xml               # AI chatbot interface

fragment_profile.xml            # User profile
fragment_settings.xml           # App settings

fragment_leaderboard.xml        # Leaderboard view

item_menu_card.xml                  # Menu item card
item_recipe_card.xml                # Recipe card
item_foodbank_card.xml              # Food bank card
item_donation.xml                   # Donation item
item_chat_message.xml               # Chat message bubble
item_leaderboard_entry.xml          # Leaderboard entry

dialog_pin_verification.xml         # PIN verification
dialog_donation_success.xml         # Success confirmation
dialog_filter.xml                   # Filter options


