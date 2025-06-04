<template>
  <div class="container px-0 sm:px-4" v-if="!loading">
    <div class="flex flex-col gap-2 p-2 rounded-lg shadow-lg bg-white">
      <div class="flex flex-col gap-2 p-4 pb-0">
        <Breadcrumbs />
        <div class="flex flex-row justify-between items-center">
          <h1
              class="font-bold text-2xl sm:text-3xl md:text-4xl text-left w-full sm:w-auto break-words truncate pb-1 -mb-1">
            {{ team.name }}
          </h1>

          <div class="flex items-center">
              <router-link :to="`/team/${team.id}/feedback`" class="w-32 h-10">
                <button class="text-gray-600 border border-gray-400 w-full h-full flex items-center justify-center rounded-md">
                  Mijn feedback
                </button>
              </router-link>
            <div class="flex space-x-2" v-if="user.roles.some(role => role === 'manager')">
            <Popper class="relative flex justify-center items-center z-0" v-if="user">
              <button class="text-black w-12 h-5 flex items-center justify-center transition duration-300 ease-in-out">
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-8">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M12 6.75a.75.75 0 1 1 0-1.5.75.75 0 0 1 0 1.5ZM12 12.75a.75.75 0 1 1 0-1.5.75.75 0 0 1 0 1.5ZM12 18.75a.75.75 0 1 1 0-1.5.75.75 0 0 1 0 1.5Z" />
                </svg>
              </button>

              <template #content>
                <div class="border border-gray-300 bg-white rounded-lg shadow-lg py-2 w-38 text-gray-800">
                  <!-- Edit Team Section -->
                  <router-link :to="`/team/${team.id}/edit`"
                               class="flex items-center text-sm text-left w-full px-4 py-2 hover:bg-gray-100 focus:outline-none">
                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="size-4">
                      <path d="M21.731 2.269a2.625 2.625 0 0 0-3.712 0l-1.157 1.157 3.712 3.712 1.157-1.157a2.625 2.625 0 0 0 0-3.712ZM19.513 8.199l-3.712-3.712-8.4 8.4a5.25 5.25 0 0 0-1.32 2.214l-.8 2.685a.75.75 0 0 0 .933.933l2.685-.8a5.25 5.25 0 0 0 2.214-1.32l8.4-8.4Z" />
                      <path d="M5.25 5.25a3 3 0 0 0-3 3v10.5a3 3 0 0 0 3 3h10.5a3 3 0 0 0 3-3V13.5a.75.75 0 0 0-1.5 0v5.25a1.5 1.5 0 0 1-1.5 1.5H5.25a1.5 1.5 0 0 1-1.5-1.5V8.25a1.5 1.5 0 0 1 1.5-1.5h5.25a.75.75 0 0 0 0-1.5H5.25Z" />
                    </svg>
                    <div class="ml-3">
                      <p class="text-sm font-medium">Aanpassen</p>
                    </div>
                  </router-link>

                  <hr class="my-1 border-gray-200">

                  <!-- Edit Form Button -->
                  <router-link :to="`/team/${team.id}/form`"
                               class="flex items-center text-sm text-left w-full px-4 py-2 hover:bg-gray-100 focus:outline-none">
                    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-5">
                      <path stroke-linecap="round" stroke-linejoin="round" d="M19.5 14.25v-2.625a3.375 3.375 0 0 0-3.375-3.375h-1.5A1.125 1.125 0 0 1 13.5 7.125v-1.5a3.375 3.375 0 0 0-3.375-3.375H8.25m2.25 0H5.625c-.621 0-1.125.504-1.125 1.125v17.25c0 .621.504 1.125 1.125 1.125h12.75c.621 0 1.125-.504 1.125-1.125V11.25a9 9 0 0 0-9-9Z" />
                    </svg>
                    <div class="ml-3">
                      <p class="text-sm font-medium">Formulier</p>
                    </div>
                  </router-link>

                  <button @click="open"
                               class="flex items-center text-sm text-left w-full px-4 py-2 hover:bg-gray-100 focus:outline-none">
                    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-5">
                      <path stroke-linecap="round" stroke-linejoin="round" d="M19.5 14.25v-2.625a3.375 3.375 0 0 0-3.375-3.375h-1.5A1.125 1.125 0 0 1 13.5 7.125v-1.5a3.375 3.375 0 0 0-3.375-3.375H8.25m.75 12 3 3m0 0 3-3m-3 3v-6m-1.5-9H5.625c-.621 0-1.125.504-1.125 1.125v17.25c0 .621.504 1.125 1.125 1.125h12.75c.621 0 1.125-.504 1.125-1.125V11.25a9 9 0 0 0-9-9Z" />
                    </svg>
                    <div class="ml-3">
                      <p class="text-sm font-medium">Rapport</p>
                    </div>
                  </button>

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
        </div>
      </div>

        <div class="container mx-auto p-4">
          <div class="mt-4">
            <ul class="space-y-4">
              <li v-for="user in sortedUsers" :key="user.id" @click="goToUserPage(user.id)">
                <div class="flex justify-between items-center p-5 border border-gray-300 rounded-lg hover:bg-gray-50 cursor-pointer">
              <span>
                {{ user.firstName }} {{ user.infix ? user.infix + ' ' : '' }} {{ user.lastName }}
              </span>
                  <div class="flex items-center space-x-4">
                <span v-if="user.roles.includes('manager')" class="text-gray-500">
                  Manager
                </span>
                  </div>
                </div>
              </li>
            </ul>
          </div>
        </div>
        <div v-if="loading" class="text-center text-gray-600">Team details worden geladen...</div>
        <div v-if="error" class="text-center text-red-500">{{ error }}</div>
      </div>
    </div>


</template>

<script setup lang="ts">
import {ref, onMounted, computed} from 'vue';
import { useRoute } from 'vue-router';
import { api } from "../AxiosInstance.ts";
import user from "../userStorage.ts";
import {useModal} from "vue-final-modal";
import ConfirmModal from "../components/modals/ConfirmModal.vue";
import router from "../router.ts";
import Breadcrumbs from '../components/Breadcrumbs.vue';
import ReportModal from "../components/modals/ReportModal.vue";

const route = useRoute();
const team = ref<Team>(null);
const loading = ref(true);
const error = ref<string | null>(null);

const fetchTeamDetails = async () => {
  loading.value = true;
  error.value = null;

  const teamId = route.params.id;

  await api.get(`/team/${teamId}`)
      .then((response) => {
        team.value = response.data;
        console.log(team.value)
      })
      .catch((err) => {
        error.value = err.message;
      })
      .finally(() => {
        loading.value = false;
      });
};

const goToUserPage = (userId: number) => {
  router.push(`/team/${team.value.id}/feedback/to/${userId}`);
};

const sortedUsers = computed(() => {
  if (team.value) {
    return [...team.value.users].sort((a, b) => {
      const isManagerA = a.roles.includes('manager') ? 1 : 0;
      const isManagerB = b.roles.includes('manager') ? 1 : 0;
      return isManagerB - isManagerA;
    });
  }
  return [];
});

const confirmDeletionTeam = async () => {
  loading.value = true;
  error.value = null;
  const teamId = route.params.id as number;

  await api.delete(`/team/${teamId}`)
      .then(() => {
          router.push("/");
      })
      .catch((err) => {
        error.value = err.message;
      })
      .finally(() => {
        loading.value = false;
      });
};


const deleteTeam = () => {
  let closeModal = () => {};

  const { open, close } = useModal({
    component: ConfirmModal,
    attrs: {
      title: 'Weet je het zeker?',
      description: `Weet je zeker dat je ${team.value.name} wilt verwijderen?`,
      icon: 'danger',
      buttons: [
        {
          text: 'Verwijderen',
          variant: 'danger',
          action: () => {
            confirmDeletionTeam();
            closeModal();
          }
        },
        {
          text: 'Annuleren',
          variant: 'secondary',
          action: () => closeModal()
        },
      ],
    }
  })

  closeModal = close;
  open();
};

let closeReportModal = () => {};

const onReportModalClose = () => {
  closeReportModal();
};

const { open, close } = useModal({
  component: ReportModal,
  attrs: {
    title: 'Rapport genereren',
    description: ``,
    icon: 'none',
    teamId: route.params.id,
    closeModal: onReportModalClose,
  },
})

closeReportModal = close;

onMounted(fetchTeamDetails);
</script>

<style scoped>
</style>
