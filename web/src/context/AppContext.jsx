import { createContext, useContext, useState, useEffect } from "react";
import { auth, db } from "../firebase";
import { onAuthStateChanged } from "firebase/auth";
import { doc, getDoc, setDoc, serverTimestamp } from "firebase/firestore";

const AppContext = createContext();

export function useApp() {
  return useContext(AppContext);
}

export function AppProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  // User States
  const [username, setUsername] = useState("");
  const [coins, setCoins] = useState(0);
  const [streak, setStreak] = useState(0);
  const [selectedPet, setSelectedPet] = useState(0); // 0 = Cat, 1 = Dog, 2 = Panda
  const [petName, setPetName] = useState("Buddy");
  const [petHappiness, setPetHappiness] = useState(0);
  const [waterIntake, setWaterIntake] = useState(0);
  const [waterGoal, setWaterGoal] = useState(8);
  const [dailySteps, setDailySteps] = useState(0);
  const [stepGoal, setStepGoal] = useState(7000);
  const [weight, setWeight] = useState("");
  const [height, setHeight] = useState("");
  const [calorieGoal, setCalorieGoal] = useState(500);
  const [customHabits, setCustomHabits] = useState([]);
  
  // Date and reward tracking
  const [lastDate, setLastDate] = useState(0);
  const [lastStreakIncrementDate, setLastStreakIncrementDate] = useState(0);
  const [goalCompletedDate, setGoalCompletedDate] = useState(0);
  const [themeMode, setThemeMode] = useState(2); // 0 = Light, 1 = Dark, 2 = System

  // Calculate active calories burned
  const weightVal = parseFloat(weight) || 70.0;
  const baseCalories = dailySteps * 0.04;
  const scale = weightVal / 70.0;
  const caloriesBurned = Math.round(baseCalories * scale);

  const getCurrentDateInt = () => {
    const date = new Date();
    const yyyy = date.getFullYear();
    const mm = String(date.getMonth() + 1).padStart(2, "0");
    const dd = String(date.getDate()).padStart(2, "0");
    return parseInt(`${yyyy}${mm}${dd}`, 10);
  };

  // Helper to sync state to Firestore and local state
  const updateStateAndSync = async (updates) => {
    if (!auth.currentUser) return;
    const uid = auth.currentUser.uid;

    // Apply locally
    if (updates.username !== undefined) setUsername(updates.username);
    if (updates.coins !== undefined) setCoins(updates.coins);
    if (updates.streak !== undefined) setStreak(updates.streak);
    if (updates.selectedPet !== undefined) setSelectedPet(updates.selectedPet);
    if (updates.petName !== undefined) setPetName(updates.petName);
    if (updates.petHappiness !== undefined) setPetHappiness(updates.petHappiness);
    if (updates.waterIntake !== undefined) setWaterIntake(updates.waterIntake);
    if (updates.waterGoal !== undefined) setWaterGoal(updates.waterGoal);
    if (updates.dailySteps !== undefined) setDailySteps(updates.dailySteps);
    if (updates.stepGoal !== undefined) setStepGoal(updates.stepGoal);
    if (updates.weight !== undefined) setWeight(updates.weight);
    if (updates.height !== undefined) setHeight(updates.height);
    if (updates.calorieGoal !== undefined) setCalorieGoal(updates.calorieGoal);
    if (updates.customHabits !== undefined) setCustomHabits(updates.customHabits);
    if (updates.lastDate !== undefined) setLastDate(updates.lastDate);
    if (updates.lastStreakIncrementDate !== undefined) setLastStreakIncrementDate(updates.lastStreakIncrementDate);
    if (updates.goalCompletedDate !== undefined) setGoalCompletedDate(updates.goalCompletedDate);
    if (updates.themeMode !== undefined) setThemeMode(updates.themeMode);

    // Sync to Firestore
    try {
      const userDocRef = doc(db, "users", uid);
      const habitsJson = updates.customHabits !== undefined ? JSON.stringify(updates.customHabits) : undefined;
      
      const firestoreData = { ...updates };
      if (habitsJson !== undefined) {
        delete firestoreData.customHabits;
        firestoreData.habitsJson = habitsJson;
      }
      
      firestoreData.lastUpdated = serverTimestamp();
      await setDoc(userDocRef, firestoreData, { merge: true });
    } catch (err) {
      console.error("Error syncing to Firestore:", err);
    }
  };

  // Check Daily Reset Operations
  const runDailyResetCheck = (data) => {
    const currentDate = getCurrentDateInt();
    const savedLastDate = data.lastDate || 0;

    if (savedLastDate !== 0 && savedLastDate !== currentDate) {
      // Determine yesterday's custom habits success
      let updatedStreak = data.streak || 0;
      const habits = data.customHabits || [];
      if (habits.length > 0) {
        const allCompleted = habits.every((h) => h.completed);
        if (!allCompleted) {
          // Reset streak if habits weren't fully cleared
          updatedStreak = 0;
        }
      }

      // Happiness decays slightly on new day (-15)
      const currentHappiness = data.petHappiness !== undefined ? data.petHappiness : 0;
      const updatedHappiness = Math.max(0, currentHappiness - 15);

      // Reset habit completion status for the new day
      const resetHabits = habits.map((h) => ({ ...h, completed: false }));

      return {
        waterIntake: 0,
        dailySteps: 0,
        petHappiness: updatedHappiness,
        customHabits: resetHabits,
        streak: updatedStreak,
        lastDate: currentDate,
        goalCompletedDate: 0 // Reset daily step goal rewards check
      };
    }
    
    // If lastDate is uninitialized, record today's date
    if (savedLastDate === 0) {
      return { lastDate: currentDate };
    }

    return null;
  };

  // Load state from Firestore on Auth
  useEffect(() => {
    const unsubscribe = onAuthStateChanged(auth, async (firebaseUser) => {
      setLoading(true);
      if (firebaseUser) {
        setUser(firebaseUser);
        const uid = firebaseUser.uid;
        
        try {
          const userDocRef = doc(db, "users", uid);
          const docSnap = await getDoc(userDocRef);
          
          if (docSnap.exists()) {
            const data = docSnap.data();
            
            // Deserialize habits list
            let habits = [];
            if (data.habitsJson) {
              try {
                habits = JSON.parse(data.habitsJson);
              } catch (e) {
                console.error("Habits deserialize error:", e);
              }
            }

            const stateFromDB = {
              username: data.username || "",
              coins: data.coins || 0,
              streak: data.streak || 0,
              selectedPet: data.selectedPet || 0,
              petName: data.petName || "Buddy",
              petHappiness: data.petHappiness !== undefined ? data.petHappiness : 0,
              waterIntake: data.waterIntake || 0,
              waterGoal: data.waterGoal || 8,
              dailySteps: data.dailySteps || 0,
              stepGoal: data.stepGoal || 7000,
              weight: data.weight || "",
              height: data.height || "",
              calorieGoal: data.calorieGoal || 500,
              customHabits: habits,
              lastDate: data.lastDate || 0,
              lastStreakIncrementDate: data.lastStreakIncrementDate || 0,
              goalCompletedDate: data.goalCompletedDate || 0,
              themeMode: data.themeMode !== undefined ? data.themeMode : 2
            };

            // Run daily reset verification
            const resetData = runDailyResetCheck(stateFromDB);
            if (resetData) {
              const mergedState = { ...stateFromDB, ...resetData };
              await updateStateAndSync(mergedState);
            } else {
              // Apply loaded states directly
              setUsername(stateFromDB.username);
              setCoins(stateFromDB.coins);
              setStreak(stateFromDB.streak);
              setSelectedPet(stateFromDB.selectedPet);
              setPetName(stateFromDB.petName);
              setPetHappiness(stateFromDB.petHappiness);
              setWaterIntake(stateFromDB.waterIntake);
              setWaterGoal(stateFromDB.waterGoal);
              setDailySteps(stateFromDB.dailySteps);
              setStepGoal(stateFromDB.stepGoal);
              setWeight(stateFromDB.weight);
              setHeight(stateFromDB.height);
              setCalorieGoal(stateFromDB.calorieGoal);
              setCustomHabits(stateFromDB.customHabits);
              setLastDate(stateFromDB.lastDate);
              setLastStreakIncrementDate(stateFromDB.lastStreakIncrementDate);
              setGoalCompletedDate(stateFromDB.goalCompletedDate);
              setThemeMode(stateFromDB.themeMode);
            }
          } else {
            // New user registration defaults
            const freshState = {
              username: firebaseUser.displayName || "",
              coins: 0,
              streak: 0,
              selectedPet: 0,
              petName: "Buddy",
              petHappiness: 0,
              waterIntake: 0,
              waterGoal: 8,
              dailySteps: 0,
              stepGoal: 7000,
              weight: "",
              height: "",
              calorieGoal: 500,
              customHabits: [],
              lastDate: getCurrentDateInt(),
              lastStreakIncrementDate: 0,
              goalCompletedDate: 0,
              themeMode: 2
            };
            await updateStateAndSync(freshState);
          }
        } catch (err) {
          console.error("Error loading user profile:", err);
        }
      } else {
        setUser(null);
        clearAllLocalStates();
      }
      setLoading(false);
    });

    return () => unsubscribe();
  }, []);

  // Theme application logic
  useEffect(() => {
    const handleThemeChange = () => {
      const root = document.documentElement;
      if (themeMode === 0) {
        root.setAttribute("data-theme", "light");
      } else if (themeMode === 1) {
        root.setAttribute("data-theme", "dark");
      } else {
        // System preference
        const isDark = window.matchMedia("(prefers-color-scheme: dark)").matches;
        root.setAttribute("data-theme", isDark ? "dark" : "light");
      }
    };

    handleThemeChange();

    // Listen to changes in system preferences if System is set
    if (themeMode === 2) {
      const media = window.matchMedia("(prefers-color-scheme: dark)");
      media.addEventListener("change", handleThemeChange);
      return () => media.removeEventListener("change", handleThemeChange);
    }
  }, [themeMode]);

  const clearAllLocalStates = () => {
    setUsername("");
    setCoins(0);
    setStreak(0);
    setSelectedPet(0);
    setPetName("Buddy");
    setPetHappiness(0);
    setWaterIntake(0);
    setWaterGoal(8);
    setDailySteps(0);
    setStepGoal(7000);
    setWeight("");
    setHeight("");
    setCalorieGoal(500);
    setCustomHabits([]);
    setLastDate(0);
    setLastStreakIncrementDate(0);
    setGoalCompletedDate(0);
    setThemeMode(2);
  };

  // Auth helper wrapper functions
  const logOutUser = async () => {
    await auth.signOut();
    clearAllLocalStates();
  };

  // MANUAL TRACKING / EMULATOR OVERRIDES
  const mockAddSteps = async (amount) => {
    const targetSteps = dailySteps + amount;
    const currentDate = getCurrentDateInt();
    const updates = { dailySteps: targetSteps };

    // Trigger step goal completion reward (+15 coins)
    if (targetSteps >= stepGoal && goalCompletedDate !== currentDate) {
      updates.goalCompletedDate = currentDate;
      updates.coins = coins + 15;
    }

    await updateStateAndSync(updates);
  };

  const incrementWater = async () => {
    const nextWater = waterIntake + 1;
    const updates = { waterIntake: nextWater };
    
    // Check if any habit contains "water" and auto-complete it!
    const list = [...customHabits];
    let changed = false;
    for (let i = 0; i < list.length; i++) {
      if (
        list[i].title.toLowerCase().includes("water") &&
        !list[i].completed &&
        nextWater >= waterGoal
      ) {
        list[i] = { ...list[i], completed: true };
        changed = true;
      }
    }

    if (changed) {
      updates.customHabits = list;
      // Triggers potential streak increment check
      const streakUpdate = await checkAndIncrementStreakLocal(list);
      if (streakUpdate) {
        Object.assign(updates, streakUpdate);
      }
    }

    await updateStateAndSync(updates);
  };

  // Habit operations
  const addHabit = async (title, reminderTime = null) => {
    if (!title.trim()) return;
    const id = crypto.randomUUID ? crypto.randomUUID() : Math.random().toString(36).substring(2);
    const newHabit = {
      id,
      title: title.trim(),
      completed: false,
      reminderTime: reminderTime // timestamp number
    };

    const updatedList = [...customHabits, newHabit];
    await updateStateAndSync({ customHabits: updatedList });

    // Request browser notification permission for reminders if desired
    if (reminderTime && "Notification" in window && Notification.permission === "default") {
      Notification.requestPermission();
    }
  };

  const deleteHabit = async (id) => {
    const updatedList = customHabits.filter((h) => h.id !== id);
    await updateStateAndSync({ customHabits: updatedList });
  };

  const editHabit = async (id, newTitle, reminderTime = null) => {
    if (!newTitle.trim()) return;
    const updatedList = customHabits.map((h) => {
      if (h.id === id) {
        return { ...h, title: newTitle.trim(), reminderTime };
      }
      return h;
    });
    await updateStateAndSync({ customHabits: updatedList });
  };

  const toggleCustomHabit = async (id) => {
    let updatedHabitCompletedStatus = false;
    const updatedList = customHabits.map((h) => {
      if (h.id === id) {
        updatedHabitCompletedStatus = !h.completed;
        return { ...h, completed: updatedHabitCompletedStatus };
      }
      return h;
    });

    const updates = { customHabits: updatedList };

    // Auto sync water if completing a water habit
    const targetHabit = customHabits.find((h) => h.id === id);
    if (targetHabit && updatedHabitCompletedStatus) {
      if (targetHabit.title.toLowerCase().includes("water")) {
        updates.waterIntake = Math.max(waterIntake, waterGoal);
      }
    }

    // Check streak updates
    const streakUpdate = await checkAndIncrementStreakLocal(updatedList);
    if (streakUpdate) {
      Object.assign(updates, streakUpdate);
    }

    await updateStateAndSync(updates);
  };

  // Local calculation for streak increases
  const checkAndIncrementStreakLocal = async (habits) => {
    if (habits.length === 0) return null;
    const allCompleted = habits.every((h) => h.completed);

    if (allCompleted) {
      const currentDate = getCurrentDateInt();
      if (lastStreakIncrementDate !== currentDate) {
        return {
          lastStreakIncrementDate: currentDate,
          streak: streak + 1,
          coins: coins + 20 // +20 coins streak completion bonus!
        };
      }
    }
    return null;
  };

  // Pet Feeding & Customizations
  const feedPet = async () => {
    if (coins < 10) return false;
    const nextHappiness = Math.min(100, petHappiness + 15);
    await updateStateAndSync({
      coins: coins - 10,
      petHappiness: nextHappiness
    });
    return true;
  };

  const renamePet = async (name) => {
    if (!name.trim()) return;
    await updateStateAndSync({ petName: name.trim() });
  };

  const changeSelectedPet = async (petIndex) => {
    await updateStateAndSync({ selectedPet: petIndex });
  };

  // Settings Goal adjustment functions
  const updateStepGoal = async (val) => {
    await updateStateAndSync({ stepGoal: Math.max(1000, Math.min(50000, val)) });
  };

  const updateWaterGoal = async (val) => {
    await updateStateAndSync({ waterGoal: Math.max(2, Math.min(24, val)) });
  };

  const updateCalorieGoal = async (val) => {
    await updateStateAndSync({ calorieGoal: Math.max(100, Math.min(10000, val)) });
  };

  const updateWeight = async (val) => {
    await updateStateAndSync({ weight: val.trim() });
  };

  const updateHeight = async (val) => {
    await updateStateAndSync({ height: val.trim() });
  };

  const updateProfile = async (name, petIndex) => {
    await updateStateAndSync({ username: name.trim(), selectedPet: petIndex });
  };

  const updateTheme = async (mode) => {
    await updateStateAndSync({ themeMode: mode });
  };

  const value = {
    user,
    loading,
    username,
    coins,
    streak,
    selectedPet,
    petName,
    petHappiness,
    waterIntake,
    waterGoal,
    dailySteps,
    stepGoal,
    weight,
    height,
    calorieGoal,
    caloriesBurned,
    customHabits,
    themeMode,
    logOutUser,
    mockAddSteps,
    incrementWater,
    addHabit,
    deleteHabit,
    editHabit,
    toggleCustomHabit,
    feedPet,
    renamePet,
    changeSelectedPet,
    updateStepGoal,
    updateWaterGoal,
    updateCalorieGoal,
    updateWeight,
    updateHeight,
    updateProfile,
    updateTheme,
    getCurrentDateInt
  };

  return (
    <AppContext.Provider value={value}>
      {!loading && children}
    </AppContext.Provider>
  );
}
