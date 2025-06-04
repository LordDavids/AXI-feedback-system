<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue';
import Breadcrumbs from '../components/Breadcrumbs.vue';
import { api } from "../AxiosInstance.ts";
import { useRouter } from 'vue-router';
import { useRoute } from 'vue-router';
import user from "../userStorage.ts";
import {useModal} from "vue-final-modal";
import ConfirmModal from "../components/modals/ConfirmModal.vue";

// Reactive state
const loading = ref(false);
const error = ref<string | null>(null);
const forms = ref<Form[]>([]);
const categories = ref<FormCategory[]>([]);
const selectedCategory = ref<string | null>(null);  // Added for category filtering
const groupedForms = ref<{ [categoryName: string]: Form[] }>({});
const router = useRouter();
const route = useRoute();
const teamId = route.params.id;
const settingsOpen = ref(false);

const fetchForms = async () => {
  loading.value = true;
  error.value = null;

  await api.get<Form[]>(`/team/${teamId}/forms`)
      .then((response) => {
        forms.value = response.data;
      })
      .catch((err) => {
        error.value = err.message;
      })
      .finally(() => {
        loading.value = false;
      });

  groupedForms.value = forms.value.reduce((groups, form) => {
    const categoryName = form.category.name;
    if (!groups[categoryName]) {
      groups[categoryName] = [];
    }
    groups[categoryName].push(form);
    return groups;
  }, {} as { [categoryName: string]: Form[] });
};

const fetchCategories = async () => {
  try {
    const response = await api.get('/category/all');
    categories.value = response.data;
  } catch (err) {
    error.value = err.message;
  }
};

const filteredForms = computed(() => {
  if (!selectedCategory.value) return forms.value;
  return forms.value.filter(form => form.category.id === selectedCategory.value);
});

function goToPublicFormsOverview() {
  router.push({ name: 'PublicFormsOverview' });
}

const confirmDeletion = (formId: number) => {
  let closeModal = () => {};

  const { open, close } = useModal({
    component: ConfirmModal,
    attrs: {
      title: 'Weet je het zeker?',
      description: `Weet je zeker dat je deze form uit het team wil verwijderen?`,
      icon: 'danger',
      buttons: [
        {
          text: 'Verwijderen',
          variant: 'danger',
          action: () => {
            deleteForm(formId);
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
  });

  closeModal = close;
  open();
};

const deleteForm = async (formId: number) => {
  loading.value = true;
  error.value = null;
  forms.value = forms.value.filter(form => form.id !== formId);

  await api.delete(`team/${teamId}/form/${formId}/remove`)
      .then(() => {
      })
      .catch((err) => {
        error.value = err.message;
      })
      .finally(() => {
        loading.value = false;
      });
}
const navigateToForm = (formId: number) => {
  router.push({ path: `/team/${route.params.id}/form/${formId}` });
};

const updateFormStatus = async (formId: number, isActive: boolean) => {
  console.log(isActive);
  loading.value = true;
  error.value = null;



  await api.put(`team/${teamId}/form/${formId}/status`, !isActive, {
    headers: {
      'Content-Type': 'application/json',
    }
  })
      .then(() => {
      })
      .catch((err) => {
        error.value = err.message;
      })
      .finally(() => {
        loading.value = false;
      });
};

const toggleSettingsOpen = () => {
  settingsOpen.value = !settingsOpen.value;
};

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
        <h1 class="text-2xl font-bold">Vragenlijst</h1>

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

          <!-- Settings Button with Dropdown Menu -->
          <Popper class="relative flex justify-center items-center z-50" v-if="user">
            <button class="text-black w-12 h-5 flex items-center justify-center transition duration-300 ease-in-out">
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-8">
                <path stroke-linecap="round" stroke-linejoin="round" d="M12 6.75a.75.75 0 1 1 0-1.5.75.75 0 0 1 0 1.5ZM12 12.75a.75.75 0 1 1 0-1.5.75.75 0 0 1 0 1.5ZM12 18.75a.75.75 0 1 1 0-1.5.75.75 0 0 1 0 1.5Z" />
              </svg>
            </button>

            <template #content>
              <div class="border border-gray-300 bg-white rounded-lg shadow-lg py-2 w-38 text-gray-800"
                   style="min-width: 200px; white-space: nowrap; z-index: 50; position: relative;">

                <!-- Edit Team Section -->
                <router-link :to="`/team/${route.params.id}/form/create`"
                             class="flex items-center text-sm text-left w-full px-4 py-2 hover:bg-gray-100 focus:outline-none">
                  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="size-4">
                    <path d="M21.731 2.269a2.625 2.625 0 0 0-3.712 0l-1.157 1.157 3.712 3.712 1.157-1.157a2.625 2.625 0 0 0 0-3.712ZM19.513 8.199l-3.712-3.712-8.4 8.4a5.25 5.25 0 0 0-1.32 2.214l-.8 2.685a.75.75 0 0 0 .933.933l2.685-.8a5.25 5.25 0 0 0 2.214-1.32l8.4-8.4Z" />
                    <path d="M5.25 5.25a3 3 0 0 0-3 3v10.5a3 3 0 0 0 3 3h10.5a3 3 0 0 0 3-3V13.5a.75.75 0 0 0-1.5 0v5.25a1.5 1.5 0 0 1-1.5 1.5H5.25a1.5 1.5 0 0 1-1.5-1.5V8.25a1.5 1.5 0 0 1 1.5-1.5h5.25a.75.75 0 0 0 0-1.5H5.25Z" />
                  </svg>
                  <div class="ml-3">
                    <p class="text-sm font-medium">Nieuwe Vragenlijst</p>
                  </div>
                </router-link>

                <hr class="my-1 border-gray-200">

                <!-- Edit Form Button -->
                <button @click="goToPublicFormsOverview"
                             class="flex items-center text-sm text-left w-full px-4 py-2 hover:bg-gray-100 focus:outline-none">
                  <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-5">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M19.5 14.25v-2.625a3.375 3.375 0 0 0-3.375-3.375h-1.5A1.125 1.125 0 0 1 13.5 7.125v-1.5a3.375 3.375 0 0 0-3.375-3.375H8.25m2.25 0H5.625c-.621 0-1.125.504-1.125 1.125v17.25c0 .621.504 1.125 1.125 1.125h12.75c.621 0 1.125-.504 1.125-1.125V11.25a9 9 0 0 0-9-9Z" />
                  </svg>
                  <div class="ml-3">
                    <p class="text-sm font-medium">Publieke Vragenlijst</p>
                  </div>
                </button>
              </div>
            </template>
          </Popper>
        </div>
      </div>

      <div v-if="error" class="text-red-500 mb-4">{{ error }}</div>

      <!-- Forms list, each row clickable to navigate -->
      <div v-for="form in filteredForms" :key="form.id" class="flex items-center justify-between mb-3 p-4 bg-white border border-gray-200 rounded-lg shadow-sm cursor-pointer hover:bg-gray-100 transition"
           @click="navigateToForm(form.id)">

        <!-- Form name with category on the right -->
        <div class="flex items-center space-x-4"> <!-- Added more space -->
          <span class="text-gray-800 text-lg font-medium">
            {{ form.name }}
          </span>
          <span class="text-gray-500 text-sm">
            {{ form.category.name }}
          </span>
        </div>

        <!-- Custom styled switch -->
        <div class="flex items-center space-x-4"> <!-- Adjusted for spacing on the right side -->
          <div class="relative">
            <input
                type="checkbox"
                v-model="form.isActive"
                :id="'form-' + form.id"
                class="absolute opacity-0 w-0 h-0"
                @click.stop="updateFormStatus(form.id, form.isActive)"
            />
            <label :for="'form-' + form.id" class="flex items-center cursor-pointer" @click.stop>
              <div class="bg-gray-300 w-12 h-6 rounded-full flex items-center p-1 transition duration-200 ease-in-out"
                   :class="{'bg-green-600': form.isActive}">
                <div class="bg-white w-4 h-4 rounded-full shadow-md transform transition duration-200 ease-in-out"
                     :class="{'translate-x-6': form.isActive, 'translate-x-0': !form.isActive}"></div>
              </div>
            </label>
          </div>

          <!-- Delete icon -->
          <button @click.stop.prevent="confirmDeletion(form.id)" class="p-2 text-red-500 hover:text-red-700 transition">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
