<script setup lang="ts">
import {PropType} from "vue";
import user from "../userStorage.ts";
import Popper from "vue3-popper";
import {api} from "../AxiosInstance.ts";
import {ref} from "vue";
import {useRouter} from "vue-router";

type LogoutResult = 'success' | 'error';

defineProps({
    user: {
        type: Object as PropType<User>,
        required: true
    }
});

const result = ref<LogoutResult | null>(null);
const router = useRouter();
const settings = () => {
  router.push({ path: "/settings" });
};
const logout = () => {
  api.post("/auth/logout", {}, {
    headers: {
      'Access-Control-Allow-Origin': '*',
      'Content-Type': 'application/json'
    }
  }).then(async () => {
    result.value = 'success';
    await router.push({path: '/login', query: {logout: 'success'}}).then(() => {
      user.value = null;
    });
  }).catch((error) => {
    console.error(error);
    result.value = 'error';
  });
};
</script>

<template>
  <Popper class="relative flex justify-center items-center" v-if="user">
      <button class="text-gray-700 font-semibold hover:text-gray-900  px-3 py-1">
        {{user.firstName}}
      </button>
    <template #content>
      <div class="border border-gray-300 bg-white rounded-lg shadow-lg py-2 w-56 text-gray-800">
        <!-- User Info Section -->
        <div class="flex items-center p-3">
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6 text-gray-600">
            <path stroke-linecap="round" stroke-linejoin="round" d="M17.982 18.725A7.488 7.488 0 0 0 12 15.75a7.488 7.488 0 0 0-5.982 2.975m11.963 0a9 9 0 1 0-11.963 0m11.963 0A8.966 8.966 0 0 1 12 21a8.966 8.966 0 0 1-5.982-2.275M15 9.75a3 3 0 1 1-6 0 3 3 0 0 1 6 0Z" />
          </svg>
          <div class="ml-3">
            <p class="text-sm font-medium">{{user.firstName}} {{user.infix}} {{user.lastName}}</p>
            <p class="text-xs text-gray-500">{{user.email}}</p>
          </div>
        </div>

        <hr class="my-1 border-gray-200">

        <!-- Settings Button -->
        <button class="text-sm text-left w-full px-4 py-2 hover:bg-gray-100 focus:outline-none" @click="settings">
          Instellingen
        </button>

        <hr class="my-1 border-gray-200">

        <!-- Logout Button -->
        <button class="text-sm text-left w-full px-4 py-2 text-red-600 hover:bg-red-50 focus:outline-none" @click="logout">
          Uitloggen
        </button>

        <!-- Error Message -->
        <p v-if="result === 'error'" class="text-xs text-red-500 text-center mt-2 px-4">Er is een fout opgetreden. Probeer het later nog een keer.</p>
      </div>
    </template>
  </Popper>
</template>

<style scoped>
.text-xs {
  font-size: 0.75rem;
}
</style>
