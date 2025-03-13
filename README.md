# UMFeed - A Mobile Application to Fight Hunger and Promote Nutrition

UMFeed is a mobile application developed as part of the WIA2007 Mobile Application Development course. The app is designed to address critical issues of hunger, food insecurity, and poor nutrition - aligned with the United Nations Sustainable Development Goals (SDG 2 and SDG 3). It specifically targets vulnerable groups such as Malaysia’s B40 community, aiming to enhance access to affordable, nutritious food while promoting sustainable food practices.

## Key Features & Functional Modules

UMFeed comprises several integrated modules, including:

- **Module 1: User Authentication**
  - Register, login, and password recovery (with both email/password and Google authentication)
  - User profile management including donation history and saved recipes

- **Module 2: Community Feeding Programs**
  - Featured “Menu Rahmah” displays on the home page
  - Dedicated list pages and filtering (by allergens, vegetarian, and halal options)
  - Real-time notifications for food offerings

- **Module 3: Food Donations**
  - Display of food banks and their inventories
  - Food donation and reservation functionalities (with PIN verification to ensure secure transactions)

- **Module 4: Community Collaboration & Awareness**
  - Impact metrics display (total donors, donations, recipients)
  - Donor leaderboard and badge reward system to foster community engagement

- **Module 5: Nutrition Education**
  - Display of affordable and nutritious recipes
  - Filtering options based on nutritional values and quick category selections
  - Save/bookmark feature for recipes

- **Module 6: Meal Planning**
  - AI-driven personalized meal planning and nutrition advice via a dedicated chatbot
  - Clear chat history and interactive UI for quick queries

## Technical Overview

- **Database Design:**  
  UMFeed uses Google Firestore as its backend, structured into collections and subcollections (e.g., users, recipes, menuRahmah, foodBanks, and chat history). The design emphasizes:
  - **Denormalization:** Optimized for fast, efficient read operations.
  - **Hierarchical Structure:** Logical grouping of related data (e.g., user donations, reservations).
  - **Scalability & Real-Time Updates:** Leveraging Firestore's real-time synchronization for dynamic app responsiveness.

- **Architecture & Code Organization:**  
  The project is built using the MVVM (Model-View-ViewModel) architectural pattern to ensure a clean separation between the UI, business logic, and data handling. The codebase also follows standardized coding styles, naming conventions, and comprehensive error handling to maintain readability and robustness.

## User Interface & Design

UMFeed’s UI is designed with simplicity and accessibility in mind:
- **Minimalist and Intuitive Layout:** Ensures ease of navigation even for non-tech savvy users.
- **Consistent Blue Color Theme:** Uses calming blues and complementary colors to evoke trust and tranquility.
- **Responsive Design:** Optimized for smartphone experiences with large clickable elements and concise text.

## Survey Insights & System Impact

- A month-long survey involving 55 UM students provided valuable feedback on the app’s design and functionality.
- Key findings indicated high user interest in features such as:
  - Regular updates on UM Menu Rahmah offerings.
  - Detailed vendor information (address, website, social media, contact numbers).
  - Food donation ease and impact visualization (leaderboards, badges).
  - AI-driven meal planning and nutritional guidance.
- These insights have guided the prioritization of features to better meet user needs and improve overall engagement.

## Team Contributions

- **Lim Hong Yu (Team Leader)**
  - Determined the overall modules and features for the mobile app.
  - Designed and implemented:
    - **Module 6: Meal Planning**
    - **Module 5: Nutrition Education**

- **Allison Low Jia Wen**
  - Analyzed survey form responses to gain valuable user insights.
  - Designed and implemented:
    - **Module 2: Community Feeding Programs**
    - **Module 4: Community Collaboration and Awareness**

- **Chu Wei Lyn**
  - Defined the non-functional requirements for the mobile app.
  - Designed and implemented **Module 3: Food Donations**.

- **Wee Dun Ying**
  - Contributed insights from the survey form responses.
  - Assisted in designing and implementing **Module 3: Food Donations**.

- **Paveendran A/L Sridharan**
  - Determined the key modules and features for the mobile app.
  - Designed and implemented **Module 1: User Authentication**.

## Conclusion

UMFeed demonstrates how digital solutions can effectively address hunger and promote healthier lifestyles by:
- Providing a seamless user experience through real-time updates and intuitive design.
- Encouraging community engagement via impact tracking and reward systems.
- Leveraging cloud-based technologies and AI to offer personalized nutrition advice and meal planning.

This repository contains the complete source code used to develop UMFeed. It is a testament to innovative mobile development aimed at making a tangible difference in food security and public health.
