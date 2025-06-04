<template>
  <div class="container px-0 sm:px-4" v-if="!loading">
    <div class="flex flex-col gap-2 p-2 rounded-lg shadow-lg bg-white">
      <!-- Breadcrumbs -->
      <div class="flex flex-row justify-between gap-2 p-4 pb-0">
        <Breadcrumbs />
        <div class="text-gray-600 text-sm font-medium">
          <span class="hidden sm:inline">Gemiddelde beoordeling: </span>
          <span class="font-bold">{{ averageScore }}</span>/5
        </div>
      </div>

      <div v-if="receiverName" class="text-center text-3xl font-bold text-gray-800 my-6 mb-0">
        {{ receiverName }}
      </div>

      <!-- Feedback List -->
      <div class="container mx-auto p-4">
        <div class="mt-4">
          <template v-if="feedback && feedback.length">
            <ul class="space-y-6">
              <li
                  v-for="item in feedback"
                  :key="item.id"
                  class="p-4 border border-gray-300 rounded-lg bg-gray-50"
              >
                <FeedbackComponent :feedback="item"/>
              </li>
            </ul>
          </template>
          <p v-else class="text-center text-gray-500">Je hebt nog geen feedback ontvangen.</p>
        </div>

        <!-- Loading and Error States -->
        <div v-if="loading" class="text-center text-gray-600">Feedback ophalen...</div>
        <div v-if="error" class="text-center text-red-500">{{ error }}</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { useRoute } from 'vue-router';
import { api } from "../AxiosInstance.ts";
import Breadcrumbs from '../components/Breadcrumbs.vue';
import Feedback from '../entities/Feedback.ts';
import FeedbackComponent from "../components/FeedbackComponent.vue";

const route = useRoute();
const feedback = ref<Feedback[]>([]);
const loading = ref(true);
const error = ref<string | null>(null);

// Variable for receiver's full name
const receiverName = ref<string>('');

// Fetch feedback and set the receiver's name
const fetchFeedback = async () => {
  console.log('Fetching feedback...');
  loading.value = true;
  error.value = null;

  const teamId = route.params.id;
  const receiverId = route.params.uId;

  const endpoint = receiverId
      ? `/team/${teamId}/feedback/all/${receiverId}`
      : `/team/${teamId}/feedback/received`;

  try {
    const response = await api.get(endpoint);
    feedback.value = response.data;

    // Assuming receiver is part of the feedback data, set the receiver's name
    if (feedback.value.length > 0 && feedback.value[0].receiver) {
      const receiver = feedback.value[0].receiver;
      receiverName.value = `${receiver.firstName} ${receiver.infix ? receiver.infix + ' ' : ''}${receiver.lastName}`;
    }
  } catch (err) {
    error.value = (err as Error).message;
  } finally {
    loading.value = false;
  }
};

const averageScore = computed(() => {
  if (feedback.value.length === 0) return "-"; // Geen feedback, geen gemiddelde
  const totalScore = feedback.value.reduce((sum, item) => sum + item.score, 0);
  return (totalScore / feedback.value.length).toFixed(1); // 1 decimaal
});

onMounted(fetchFeedback);
</script>

<style scoped>
</style>
