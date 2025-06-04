<script setup lang="ts">
import { ref, computed } from 'vue';
import { useRoute } from 'vue-router';
import user from "../userStorage.ts";
import UserPopper from "./UserPopper.vue";

const isMenuOpen = ref(false);
const route = useRoute();

const toggleMenu = () => {
  isMenuOpen.value = !isMenuOpen.value;
};

const isActive = (path: string) => {
  return computed(() => route.path === path);
};
</script>

<template>
  <nav class="bg-white shadow-md" v-if="user">
    <div class="w-full mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex justify-between h-16">
        <div class="flex">
          <div class="flex-shrink-0">
            <router-link to="/">
              <img class="h-8 mt-4" src="/AXI_logo.png" alt="Logo">
            </router-link>
          </div>
          <div class="hidden sm:ml-6 sm:flex sm:space-x-8">
            <router-link :to="'/'" :class="[{ 'border-axi-purple text-gray-900': isActive('/').value, 'border-transparent text-gray-500 hover:text-gray-700': !isActive('/').value }, 'inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium']">Teams</router-link>
            <router-link :to="'/users'" v-if="user.roles.includes('manager') || user.roles.includes('admin')" :class="[{ 'border-axi-purple text-gray-900': isActive('/users').value, 'border-transparent text-gray-500 hover:text-gray-700': !isActive('/users').value }, 'inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium']">Medewerkers</router-link>
            <router-link :to="'/register'" v-if="user.roles.includes('admin')" :class="[{ 'border-axi-purple text-gray-900': isActive('/register').value, 'border-transparent text-gray-500 hover:text-gray-700': !isActive('/register').value }, 'inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium']">Registeren</router-link>
          </div>
        </div>
        
        <span class="hidden sm:inline-block content-center">
          <UserPopper :user="user"/>
        </span>
        
        <div class="-mr-2 flex items-center sm:hidden">
          <button @click="toggleMenu" class="bg-white inline-flex items-center justify-center p-2 rounded-md text-gray-400 hover:text-gray-500 hover:bg-gray-100 focus:outline-none focus:ring-2 focus:ring-inset focus:ring-indigo-500">
            <span class="sr-only">Open main menu</span>
            <svg class="h-6 w-6" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor" aria-hidden="true">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16m-7 6h7" />
            </svg>
          </button>
        </div>
      </div>
    </div>

    <div v-if="isMenuOpen" class="sm:hidden">
      <div class="pt-2 pb-3 space-y-1">
        <router-link :to="'/'" :class="[{ 'bg-indigo-50 border-axi-purple text-indigo-700': isActive('/').value, 'border-transparent text-gray-500 hover:bg-gray-50 hover:border-gray-300 hover:text-gray-700': !isActive('/').value }, 'block pl-3 pr-4 py-2 border-l-4 text-base font-medium']">Home</router-link>
        <router-link :to="'/users'" v-if="user.roles.includes('manager') || user.roles.includes('admin')" :class="[{ 'bg-indigo-50 border-axi-purple text-indigo-700': isActive('/users').value, 'border-transparent text-gray-500 hover:bg-gray-50 hover:border-gray-300 hover:text-gray-700': !isActive('/users').value }, 'block pl-3 pr-4 py-2 border-l-4 text-base font-medium']">Medewerkers</router-link>
        <router-link :to="'/register'" v-if="user.roles.includes('admin')" :class="[{ 'bg-indigo-50 border-axi-purple text-indigo-700': isActive('/register').value, 'border-transparent text-gray-500 hover:bg-gray-50 hover:border-gray-300 hover:text-gray-700': !isActive('/register').value }, 'block pl-3 pr-4 py-2 border-l-4 text-base font-medium']">Registreren</router-link>
        <div class="block pl-3 pr-4 py-2 border-l-4 border-transparent text-base font-medium">
          <UserPopper :user="user"/>
        </div>
      </div>
    </div>
  </nav>
</template>

<style scoped>
/* Add any additional custom styles here */
.border-axi-purple {
  border-color: #7A36FF;
}
</style>