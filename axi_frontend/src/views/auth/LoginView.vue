<script setup lang="ts">
import { ref } from 'vue';
import {useRoute, useRouter} from 'vue-router';
import axios from "axios";
import user from "../../userStorage.ts";
import {api} from "../../AxiosInstance.ts";

const route = useRoute();
const router = useRouter();
const errorMessage = ref('');

const handleSubmit = (event: Event) => {
  const target = event.target as HTMLFormElement;
  
  const email = target.email.value;
  const password = target.password.value;

  // Send the email and password to the server
  api.post<user>(import.meta.env.VITE_API_URL + "/auth/login", {
    email,
    password
  }, {
    headers: {
      'Access-Control-Allow-Origin': '*',
      'Content-Type': 'application/json'
    }
  }).then((response) => {
    user.value = response.data;

    // Redirect the user to the home page
    router.push("/");
  }).catch((error) => {
    if(error?.response?.status === 401) {
      errorMessage.value = "Incorrecte inloggegevens.";
      return;
    }

    if (error?.response?.status === 500) {
      console.error(error)
      errorMessage.value = "Er is een fout opgetreden bij de server.";
      return;
    }

    if(error?.response?.status === 400) {
      errorMessage.value = "Vul alle velden in.";
      return;
    }

    if (error?.response?.status === null || error?.response?.status === undefined) {
      console.error(error);
      errorMessage.value = "Er is een onbekende fout opgetreden. Probeer het later nog een keer.";
      return;
    }


    errorMessage.value = error?.response?.data?.message;
  });
};
</script>

<template>
  <div class="mt-20">
    <img src="/AXI_logo.png" alt="Logo" class="login-image mx-auto w-full max-w-sm h-auto mb-8">
    <span v-if="route?.query?.logout === 'success'">Je bent succesvol uitgelogd!</span>
    <span v-if="route?.query?.logout === 'expired'">De sessie is verlopen</span>
    <div class="flex flex-col items-center justify-center mt-8">
      <div class="w-full max-w-sm bg-white p-6 rounded-lg shadow-lg">
        <form id="login-form" @submit.prevent="handleSubmit" class="flex flex-col space-y-4">
          <div class="form-group">
            <label for="email" class="block text-sm font-medium text-gray-700">Email:</label>
            <input type="email" id="email" required class="mt-1 block w-full p-2 border border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm">
          </div>
          <div class="form-group">
            <label for="password" class="block text-sm font-medium text-gray-700">Password:</label>
            <input type="password" id="password" required class="mt-1 block w-full p-2 border border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm">
          </div>
          <button type="submit" class="w-full AXI-bg AXI-bg-hover text-white py-2 px-4 rounded-md  focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500">Sign In</button>
          <p v-if="errorMessage" id="error-message" class="text-red-500 text-sm">{{errorMessage}}</p>
        </form>
      </div>
    </div>
    
  </div>
</template>

<style scoped>
</style>
