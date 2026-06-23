import { initializeApp } from "firebase/app";

import { getAuth } from "firebase/auth";
import { getFirestore } from "firebase/firestore";

const firebaseConfig = {

  apiKey: "AIzaSyBrtO7Ix5IXEuWmX9ukYnEy7pazR7NQqs0",

  authDomain: "fitnesspaw-2faa2.firebaseapp.com",

  projectId: "fitnesspaw-2faa2",

  storageBucket: "fitnesspaw-2faa2.firebasestorage.app",

  messagingSenderId: "1045250132329",

  appId: "1:1045250132329:web:a2da01e8fa8b22e85b95f8",

  measurementId: "G-F3L4WMLFPK"
};

const app = initializeApp(firebaseConfig);

export const auth = getAuth(app);
export const db = getFirestore(app);