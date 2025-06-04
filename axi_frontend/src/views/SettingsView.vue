<script setup lang="ts">
import { ref } from "vue";
import { api } from "../AxiosInstance";
import user from "../userStorage";

const currentPassword = ref("");
const newPassword = ref("");
const repeatPassword = ref("");
const errorMessage = ref("");
const successMessage = ref("");

const changePassword = () => {
  errorMessage.value = "";
  successMessage.value = "";
  
  if (newPassword.value !== repeatPassword.value) {
    errorMessage.value = "Wachtwoorden komen niet overeen.";
    return;
  }

  api.post("/auth/change-password", {
    oldPassword: currentPassword.value,
    newPassword: newPassword.value
  })
      .then(response => {
        if (response.status === 200) {
          successMessage.value = "Wachtwoord succesvol aangepast!";
          currentPassword.value = "";
          newPassword.value = "";
        } else {
          errorMessage.value = "Onverwacht antwoord van de server.";
        }
      })
      .catch(error => {
        console.error(error);
        if (!error?.response?.data) {
          errorMessage.value = "Er is een onbekende fout opgetreden. Probeer het later opnieuw.";
          return;
        }

        if (error.response.status === 400 || error.response.status === 401) {
          errorMessage.value = "Het wachtwoord dat u heeft ingevoerd is incorrect, probeer het nog een keer.";
        } else if (error.response.status === 500) {
          errorMessage.value = "Er is een onbekende fout opgetreden. Probeer het later opnieuw.";
        } else {
          errorMessage.value = error.response.data.message;
        }
      });
};
</script>


<template>
  <div class="settings-page mt-20">
    <h1 class="text-center text-2xl font-bold mb-6">Instellingen</h1>
    <p v-if="user" class="text-center mb-4">{{ user.firstName }} {{ user.infix }} {{ user.lastName }}</p>
    <p v-else class="text-center text-gray-500 mb-4">Geen gebruikers informatie gevonden.</p>

    <div class="flex flex-col items-center justify-center">
      <div class="w-full max-w-sm bg-white p-6 rounded-lg shadow-lg">
        <form @submit.prevent="changePassword" class="flex flex-col space-y-4">
          <div class="form-group">
            <label for="current-password" class="block text-sm font-medium text-gray-700">Huidig wachtwoord:</label>
            <input
                id="current-password"
                type="password"
                v-model="currentPassword"
                required
                class="mt-1 block w-full p-2 border border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
            />
          </div>

          <div class="form-group">
            <label for="new-password" class="block text-sm font-medium text-gray-700">Nieuw wachtwoord:</label>
            <input
                id="new-password"
                type="password"
                v-model="newPassword"
                required
                class="mt-1 block w-full p-2 border border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
            />
          </div>

          <div class="form-group">
            <label for="repeat-password" class="block text-sm font-medium text-gray-700">Herhaal nieuw wachtwoord:</label>
            <input
                id="repeat-password"
                type="password"
                v-model="repeatPassword"
                required
                class="mt-1 block w-full p-2 border border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
            />
            <p v-if="newPassword !== repeatPassword" class="text-red-500 text-sm mt-2">Wachtwoorden komen niet overeen.</p>
          </div>

          <button
              type="submit"
              class="w-full bg-indigo-600 text-white py-2 px-4 rounded-md hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
          >
            Verander wachtwoord
          </button>

          <!-- Error or Success Messages -->
          <p v-if="errorMessage" class="text-red-500 text-sm mt-2">{{ errorMessage }}</p>
          <p v-if="successMessage" class="text-green-500 text-sm mt-2">{{ successMessage }}</p>
        </form>
      </div>
    </div>
  </div>
</template>