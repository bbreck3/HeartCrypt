# HeartCrypt
Rearch Project that was desing to explore homomorphic encryption  possibilities on the Android platform. 
Technolpogy used:

Smartwatch: LG G Watch R
Smartphone: Samsung Galaxy s5

Android API Restrictions:
Due to the desing of the smart watch android wear applictation, this project will only work on Android Wear API 
Level 23 or higher.

Project Overview:
The Smartwatch measures your heart rate and sends this data to the phone. The phone then takes this date, encrypts it, and submits the encrypted data along with a public key to a server. The server then utilize the pailier cryptosystem using the public key to sum the encrypted data vectors. Once the sum is complete, the encrypted result is then sent back to the phone for decryption which is displayed at the bottom of the app.

Future Goals:
-Implement RSA and AES encryption schemes to test the speeds of encryption.
-Implement a fully homomorphic encryption scheme based the the work completed by Craid Gentry.

Paper: https://crypto.stanford.edu/craig/craig-thesis.pdf


#Instruction to Get Started for future development and contribution
* Follow instructions here to [set up ADB on your device](https://developer.android.com/training/wearables/apps/creating.html), with [bluetooth](https://developer.android.com/training/wearables/apps/bt-debugging.html), if necessary
* Clone the repo, open in Android studio, and launch the "wear" app
* You should see messages on LogCat for the wearable
