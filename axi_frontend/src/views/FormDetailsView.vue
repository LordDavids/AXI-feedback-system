<template>
  <div v-if="!loading">
    <div class="container px-0 sm:px-4">
      <div class="flex flex-col gap-2 p-2 rounded-lg shadow-lg bg-white">
        <Breadcrumbs />
        <div class="flex flex-row justify-between p-4">
            <h1 class="font-bold text-2xl sm:text-3xl md:text-4xl text-left w-full sm:w-auto break-words truncate pb-1 -mb-1">{{form.name}} - {{ form.category.name }}</h1>
            <div v-if="user.roles.some(role => role === 'manager') && !form.isPublic" class="flex space-x-2">
              <Popper class="relative flex justify-center items-center z-0" v-if="user">
                <button class="text-black w-12 h-12 flex items-center justify-center transition duration-300 ease-in-out">
                  <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-8">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M12 6.75a.75.75 0 1 1 0-1.5.75.75 0 0 1 0 1.5ZM12 12.75a.75.75 0 1 1 0-1.5.75.75 0 0 1 0 1.5ZM12 18.75a.75.75 0 1 1 0-1.5.75.75 0 0 1 0 1.5Z" />
                  </svg>
                </button>

                <template #content>
                  <div class="border border-gray-300 bg-white rounded-lg shadow-lg py-2 w-38 text-gray-800">

                    <!-- Edit Team Section -->
                    <router-link :to="`/team/${route.params.id}/form/${form.id}/edit`"
                                 class="flex items-center text-sm text-left w-full px-4 py-2 hover:bg-gray-100 focus:outline-none">
                      <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="size-5">
                        <path d="M21.731 2.269a2.625 2.625 0 0 0-3.712 0l-1.157 1.157 3.712 3.712 1.157-1.157a2.625 2.625 0 0 0 0-3.712ZM19.513 8.199l-3.712-3.712-8.4 8.4a5.25 5.25 0 0 0-1.32 2.214l-.8 2.685a.75.75 0 0 0 .933.933l2.685-.8a5.25 5.25 0 0 0 2.214-1.32l8.4-8.4Z" />
                        <path d="M5.25 5.25a3 3 0 0 0-3 3v10.5a3 3 0 0 0 3 3h10.5a3 3 0 0 0 3-3V13.5a.75.75 0 0 0-1.5 0v5.25a1.5 1.5 0 0 1-1.5 1.5H5.25a1.5 1.5 0 0 1-1.5-1.5V8.25a1.5 1.5 0 0 1 1.5-1.5h5.25a.75.75 0 0 0 0-1.5H5.25Z" />
                      </svg>
                      <div class="ml-3">
                        <p class="text-sm font-medium">Aanpassen</p>
                      </div>
                    </router-link>

                    <hr class="my-1 border-gray-200">

                    <!-- Delete Button -->
                    <button class="flex items-center text-sm text-left w-full px-3.5 py-2 text-red-600 hover:bg-red-50 focus:outline-none font-medium" @click="deleteTeam">
                      <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-5 mr-3">
                        <path stroke-linecap="round" stroke-linejoin="round" d="m14.74 9-.346 9m-4.788 0L9.26 9m9.968-3.21c.342.052.682.107 1.022.166m-1.022-.165L18.16 19.673a2.25 2.25 0 0 1-2.244 2.077H8.084a2.25 2.25 0 0 1-2.244-2.077L4.772 5.79m14.456 0a48.108 48.108 0 0 0-3.478-.397m-12 .562c.34-.059.68-.114 1.022-.165m0 0a48.11 48.11 0 0 1 3.478-.397m7.5 0v-.916c0-1.18-.91-2.164-2.09-2.201a51.964 51.964 0 0 0-3.32 0c-1.18.037-2.09 1.022-2.09 2.201v.916m7.5 0a48.667 48.667 0 0 0-7.5 0" />
                      </svg>
                      Verwijderen
                    </button>

                    <!-- Error Message -->
                    <p v-if="result === 'error'" class="text-xs text-red-500 text-center mt-2 px-4">Er is een fout opgetreden. Probeer het later nog een keer.</p>
                  </div>
                </template>
              </Popper>
            </div>
        </div>

        <div class="container mx-auto p-4">
          <div class="mt-4">
            <ul class="space-y-4">
              <li v-for="question in form.questions" :key="question.id">
                <div class="flex justify-between items-center p-5 border border-gray-300 rounded-lg hover:bg-gray-50 cursor-pointer">
              <span>
                {{ question.question }}
              </span>
                </div>
              </li>
            </ul>
          </div>
        </div>
        <div v-if="loading" class="text-center text-gray-600">Loading team details...</div>
        <div v-if="error" class="text-center text-red-500">{{ error }}</div>
      </div>
    </div>
  </div>

</template>

<script setup lang="ts">
import {ref, onMounted, computed} from 'vue';
import { useRoute } from 'vue-router';
import { api } from "../AxiosInstance.ts";
import user from "../userStorage.ts";
import Breadcrumbs from '../components/Breadcrumbs.vue';


const route = useRoute();
const form = ref<Form>(null);
const loading = ref(true);
const error = ref<string | null>(null);

const fetchFormDetails = async () => {
  loading.value = true;
  error.value = null;

  const id = 0;
  const formId = route.params.fId;

  await api.get(`/team/${id}/form/${formId}`)
      .then((response) => {
        form.value = response.data;
        console.log(form.value)
      })
      .catch((err) => {
        error.value = err.message;
      })
      .finally(() => {
        loading.value = false;
      });
};

onMounted(fetchFormDetails);
</script>

<style scoped>
</style>
