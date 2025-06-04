<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { api } from "../AxiosInstance.ts";
import { useRouter } from 'vue-router';
import { useRoute } from 'vue-router';
import Breadcrumbs from '../components/Breadcrumbs.vue';

// Reactive state
const forms = ref<Form[]>([]);
const categories = ref<FormCategory[]>([]);
const selectedCategory = ref<string | null>(null);
const loading = ref(false);
const error = ref<string | null>(null);

const router = useRouter();
const route = useRoute();
// Fetch all forms on component mount
const fetchForms = async () => {
  loading.value = true;
  error.value = null;

  await api.get('/team/forms')
      .then((response) => {
        forms.value = response.data;
      })
      .catch((err) => {
        error.value = err.message;
      })
      .finally(() => {
        loading.value = false;
      });
};

// Fetch all categories on component mount
const fetchCategories = async () => {
  try {
    const response = await api.get('/category/all');
    categories.value = response.data;
  } catch (err) {
    error.value = err.message;
  }
};

const addFormToTeam = async (formId: number) => {
  const teamId = route.params.id;
  try {
    await api.post(`/team/${teamId}/form/${formId}/add`);
    await router.push(`/team/${teamId}/form`);
  } catch (err) {
    alert('Failed to add form to team.');
  }
};

// Filtered forms based on selected category
const filteredForms = computed(() => {
  if (!selectedCategory.value) return forms.value;
  return forms.value.filter(form => form.category.id === selectedCategory.value);
});

// Fetch forms and categories on page load
onMounted(() => {
  fetchForms();
  fetchCategories();
});

</script>

<template>
  <div class="container px-0 sm:px-4">
    <div class="flex flex-col gap-2 p-6 rounded-lg shadow-lg bg-white">
      <Breadcrumbs />
      <div class="flex justify-between items-center mb-4">
        <!-- Left-aligned Title -->
        <h1 class="text-2xl font-bold">Publieke Vragenlijst</h1>

        <!-- Right-aligned Category and Settings Button -->
        <div class="flex items-center space-x-4">
          <!-- Category selection dropdown -->
          <div>
            <label for="category" class="sr-only">Selecteer Categorie:</label>
            <select id="category" v-model="selectedCategory" class="p-2 border border-gray-300 rounded-lg">
              <option :value="null">All</option>
              <option v-for="category in categories" :key="category.id" :value="category.id">
                {{ category.name }}
              </option>
            </select>
          </div>
          <div class="relative">
            <router-link
                :to="`/team/${route.params.id}/form/create`"
                class="block w-full text-left px-4 py-2 AXI-bg AXI-bg-hover rounded-lg text-white transition"
            >
              Nieuwe Vragenlijst
            </router-link>
          </div>
        </div>
      </div>

      <!-- Display forms filtered by selected category -->
      <ul v-if="!loading" class="space-y-4">

        <!-- Forms list, each row clickable to navigate -->
        <div v-for="form in filteredForms" :key="form.id" class="flex items-center justify-between mb-3 p-4 bg-white border border-gray-200 rounded-lg shadow-sm cursor-pointer hover:bg-gray-100 transition"
             @click="router.push('/team/' + route.params.id + '/form/' + form.id)">

          <!-- Form name with category on the right -->
          <div class="flex items-center space-x-4"> <!-- Added more space -->
            <span class="text-gray-800 text-lg font-medium">
            {{ form.name }}
            </span>
            <span class="text-gray-500 text-sm">
            {{ form.category.name }}
            </span>
          </div>

          <div class="flex items-center space-x-4">
            <button
                @click="addFormToTeam(form.id)"
                class="p-2 rounded-full AXI-bg AXI-bg-hover transition text-white"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.5v15m7.5-7.5h-15" />
              </svg>
            </button>
          </div>
        </div>
      </ul>
      <div v-if="!loading && filteredForms.length === 0" class="text-center text-gray-500">No forms available for this category.</div>
    </div>
  </div>
</template>

