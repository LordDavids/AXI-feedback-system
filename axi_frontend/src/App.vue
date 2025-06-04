<script setup lang="ts">

import Navbar from "./components/Navbar.vue";
import {useRoute} from "vue-router";
const route = useRoute()

import { type IStaticMethods } from "preline/preline";
import {onMounted} from "vue";
import {ModalsContainer} from "vue-final-modal";
declare global {
  interface Window {
    HSStaticMethods: IStaticMethods;
  }
}

onMounted(() => {
  setTimeout(() => {
    window.HSStaticMethods.autoInit();
  }, 100)
});



</script>

<template>
  <Navbar v-if="!route?.meta?.hideNav"/>
  
  <div class="text-center w-full h-fit py-2 sm:py-8 mt-0 mb-0
      px-0
      sm:px-20
      md:px-36
      lg:px-52
      xl:px-72
      2xl:px-96">
    <RouterView class="mr-auto ml-auto"/>
  </div>
  
  <ModalsContainer/>
</template>


<style scoped>

</style>
