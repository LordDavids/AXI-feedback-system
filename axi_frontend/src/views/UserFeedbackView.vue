<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { api } from "../AxiosInstance.ts";
import { useRouter } from 'vue-router';
import { useRoute } from 'vue-router';
import Breadcrumbs from '../components/Breadcrumbs.vue';
import user from "../userStorage.ts";
import {useModal} from "vue-final-modal";
import ReportModal from "../components/modals/ReportModal.vue";
import FeedbackComponent from "../components/FeedbackComponent.vue";

// Reactive state
const feedback = ref<Feedback[]>([]);
const loading = ref(false);
const error = ref<string | null>(null);
const receiverName = ref<string>('');

const router = useRouter();
const route = useRoute();

const fetchFeedback = async () => {
  loading.value = true;
  error.value = null;

  const teamId = route.params.id;
  const receiverId = route.params.Uid;

  try {
    const response = await api.get(`/team/${teamId}/feedback/sent/${receiverId}`);
    feedback.value = response.data;

    if (feedback.value.length > 0 && feedback.value[0].receiver) {
      const receiver = feedback.value[0].receiver;
      receiverName.value = `${receiver.firstName} ${receiver.infix ? receiver.infix + ' ' : ''}${receiver.lastName}`;
    }
  } catch (err) {
    error.value = err.message;
  } finally {
    loading.value = false;
  }
};

let closeModal = () => {};

const onClose = () => {
  closeModal();
};

const { open, close } = useModal({
  component: ReportModal,
  attrs: {
    title: 'Rapport genereren',
    description: ``,
    icon: 'none',
    teamId: route.params.id,
    userId: route.params.Uid,
    closeModal: onClose,
  },
})

closeModal = close;

// Fetch feedback on component mount
onMounted(() => {
  fetchFeedback();
});
</script>

<template>
  <div class="container px-0 sm:px-4">
    <div class="flex flex-col gap-2 p-6 rounded-lg shadow-lg bg-white">
      <Breadcrumbs />
      <div class="flex justify-between items-center mb-2 py-1">
        <!-- Left-aligned Title -->
        <h1 class="text-2xl font-bold">Feedback <span v-if="receiverName">aan {{receiverName}}</span></h1>

        <!-- Right-aligned "Give Feedback" Button -->
        <div class="flex items-center space-x-4">
          <Popper class="relative flex justify-center items-center z-0" v-if="user">
            <button class="text-black w-12 h-5 flex items-center justify-center transition duration-300 ease-in-out">
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-8">
                <path stroke-linecap="round" stroke-linejoin="round" d="M12 6.75a.75.75 0 1 1 0-1.5.75.75 0 0 1 0 1.5ZM12 12.75a.75.75 0 1 1 0-1.5.75.75 0 0 1 0 1.5ZM12 18.75a.75.75 0 1 1 0-1.5.75.75 0 0 1 0 1.5Z" />
              </svg>
            </button>

            <template #content>
              <div class="border border-gray-300 bg-white rounded-lg shadow-lg py-2 w-40 text-gray-800">

                <router-link :to="`/team/${route.params.id}/feedback/${route.params.Uid}`"
                             class="flex items-center text-sm text-left w-full px-4 py-2 hover:bg-gray-100 focus:outline-none">
                  <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-5">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M2.036 12.322a1.012 1.012 0 0 1 0-.639C3.423 7.51 7.36 4.5 12 4.5c4.638 0 8.573 3.007 9.963 7.178.07.207.07.431 0 .639C20.577 16.49 16.64 19.5 12 19.5c-4.638 0-8.573-3.007-9.963-7.178Z" />
                    <path stroke-linecap="round" stroke-linejoin="round" d="M15 12a3 3 0 1 1-6 0 3 3 0 0 1 6 0Z" />
                  </svg>
                  <div class="ml-3">
                    <p class="text-sm font-medium">Alles inzien</p>
                  </div>
                </router-link>

                <hr class="my-1 border-gray-200">

                <button @click="open" class="flex items-center text-sm text-left w-full px-4 py-2 hover:bg-gray-100 focus:outline-none">
                  <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-5">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M19.5 14.25v-2.625a3.375 3.375 0 0 0-3.375-3.375h-1.5A1.125 1.125 0 0 1 13.5 7.125v-1.5a3.375 3.375 0 0 0-3.375-3.375H8.25m.75 12 3 3m0 0 3-3m-3 3v-6m-1.5-9H5.625c-.621 0-1.125.504-1.125 1.125v17.25c0 .621.504 1.125 1.125 1.125h12.75c.621 0 1.125-.504 1.125-1.125V11.25a9 9 0 0 0-9-9Z" />
                  </svg>
                  <div class="ml-3">
                    <p class="text-sm font-medium">Rapport</p>
                  </div>
                </button>

                <hr class="my-1 border-gray-200">

                <router-link :to="`/team/${route.params.id}/feedback/${route.params.Uid}/create`"
                             class="flex items-center text-sm text-left w-full px-4 py-2 hover:bg-gray-100 focus:outline-none">
                  <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-5">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M6.633 10.25c.806 0 1.533-.446 2.031-1.08a9.041 9.041 0 0 1 2.861-2.4c.723-.384 1.35-.956 1.653-1.715a4.498 4.498 0 0 0 .322-1.672V2.75a.75.75 0 0 1 .75-.75 2.25 2.25 0 0 1 2.25 2.25c0 1.152-.26 2.243-.723 3.218-.266.558.107 1.282.725 1.282m0 0h3.126c1.026 0 1.945.694 2.054 1.715.045.422.068.85.068 1.285a11.95 11.95 0 0 1-2.649 7.521c-.388.482-.987.729-1.605.729H13.48c-.483 0-.964-.078-1.423-.23l-3.114-1.04a4.501 4.501 0 0 0-1.423-.23H5.904m10.598-9.75H14.25M5.904 18.5c.083.205.173.405.27.602.197.4-.078.898-.523.898h-.908c-.889 0-1.713-.518-1.972-1.368a12 12 0 0 1-.521-3.507c0-1.553.295-3.036.831-4.398C3.387 9.953 4.167 9.5 5 9.5h1.053c.472 0 .745.556.5.96a8.958 8.958 0 0 0-1.302 4.665c0 1.194.232 2.333.654 3.375Z" />
                  </svg>
                  <div class="ml-3">
                    <p class="text-sm font-medium">Geef feedback</p>
                  </div>
                </router-link>
              </div>
            </template>
          </Popper>
        </div>

      </div>

      <!-- Display feedback list -->
      <div class="container mx-auto p-4 pt-0" v-if="feedback && feedback.length">
        <div class="mt-4">
          <ul class="space-y-6">
            <li
                v-for="item in feedback"
                :key="item.id"
                class="p-4 border border-gray-300 rounded-lg bg-gray-50"
            >
              <FeedbackComponent :feedback="item" />
            </li>
          </ul>
        </div>

        <!-- Loading and Error States -->
        <div v-if="loading" class="text-center text-gray-600">Feedback ophalen...</div>
        <div v-if="error" class="text-center text-red-500">{{ error }}</div>
      </div>

      <div v-if="!loading && feedback.length === 0" class="text-center text-gray-500">
        Geen feedback verstuurd naar deze gebruiker.
      </div>
    </div>
  </div>
</template>
