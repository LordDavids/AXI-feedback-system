<script setup lang="ts">
import {computed, onMounted, ref, watch} from 'vue';
import { api } from "../AxiosInstance.ts";
import { useRouter } from 'vue-router';
import { useRoute } from 'vue-router';
import Breadcrumbs from '../components/Breadcrumbs.vue';
import {useModal} from "vue-final-modal";
import ConfirmModal from "../components/modals/ConfirmModal.vue";

// Reactive state
const formName = ref<string>('');
const loading = ref(false);
const error = ref<string | null>(null);
const searchTerm = ref<string>('');
const questions = ref<Question[]>([]);
const selectedQuestions = ref<Question[]>([]);
const form = ref<Form>(null);
const categories = ref<FormCategory[]>([]);
const selectedCategory = ref<number | null>(null);
const newQuestion = ref<string>('');
const isPublic = ref<boolean>(false);
const isNewQuestionPublic = ref<boolean>(false);

const router = useRouter();
const route = useRoute();


const editForm = async () => {
  loading.value = true;
  error.value = null;

  const id = route.params.id;

  await api.post(`/team/${id}/form/create`, {
    name: formName.value,
    questionIds: selectedQuestions.value.map(question => question.id),
    categoryId: selectedCategory.value,
    isActive: true,
    teamIds: [id],
    isPublic: isPublic.value
  })
      .then((response) => {
        router.push(`/team/${id}/form`);
      })
      .catch((err) => {
        error.value = err.message;
      })
      .finally(() => {
        loading.value = false;
      });
};

const filteredQuestions = computed(() => {
  return questions.value.filter(question =>
      !selectedQuestions.value.some(selectedQuestion => selectedQuestion.id === question.id));
});

const fetchCategories = async () => {
  error.value = null;

  await api.get(`/category/all`)
      .then((response) => {
        console.log(response.data);
        categories.value = response.data;
        console.log(categories.value)
      })
      .catch((err) => {
        error.value = err.message;
      })
      .finally(() => {
        loading.value = false;
      });
};

// Handle form submission
const handleSubmit = (e: Event) => {
  e.preventDefault(); // Prevent default form submission
  editForm(); // Call the createTeam method
};

let debounceTimeout: ReturnType<typeof setTimeout>;

const searchQuestions = async () => {
  error.value = null;

  await api.get<Question[]>('/question/search', {
    params: {
      query: searchTerm.value.trim(),
    }
  })
      .then((response) => {
        questions.value = response.data;
        console.log(questions.value);
      })
      .catch((err) => {
        error.value = err.message;
      })
};

const confirmDeletion = (question: Question) => {
  const index = selectedQuestions.value.findIndex(selectedQuestion => selectedQuestion.id === question.id);
  if (index !== -1) {
    selectedQuestions.value.splice(index, 1);
  }
  searchQuestions();
}

const removeQuestionFromList = (question: Question) => {
  let closeModal = () => {};

  const { open, close } = useModal({
    component: ConfirmModal,
    attrs: {
      title: 'Weet je het zeker?',
      description: `Weet je zeker dat je '${question.question}' wilt verwijderen?`,
      icon: 'danger',
      buttons: [
        {
          text: 'Verwijderen',
          variant: 'danger',
          action: () => {
            confirmDeletion(question);
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

const debouncedSearchQuestions = () => {
  clearTimeout(debounceTimeout);
  debounceTimeout = setTimeout(() => {
    searchQuestions();
  }, 200);
};

const addQuestionToList = (question: Question) => {
  if (!selectedQuestions.value.some(selectedQuestion => selectedQuestion.id === question.id)) {
    selectedQuestions.value.push(question);
  }
};

const addCustomQuestion = async () => {
  if (newQuestion.value.trim()) {
    try {
      loading.value = true;
      const response = await api.post("/question/create", {
            question: newQuestion.value,
            isPublic: isNewQuestionPublic.value
          }
      );
      selectedQuestions.value.push(response.data);
      newQuestion.value = '';
    } catch (err) {
      error.value = err.message;
    } finally {
      loading.value = false;
    }
  }
};

watch(searchTerm, debouncedSearchQuestions);

onMounted(() => {
  fetchCategories();
  searchQuestions();
});

const goBack = () => {
  router.go(-1);
};


</script>

<template>
  <div class="flex items-center">
    <div class="container px-0 sm:px-4 ">
      <div class="flex flex-col gap-4 p-6 rounded-lg shadow-lg bg-white">
        <div>
          <div class="flex items-center justify-between mb-4">
            <Breadcrumbs class="font-medium"/>
            <div class="flex items-center cursor-pointer">
              <label :for="'public-toggle'" class="flex justify-end items-center w-full cursor-pointer">
                <label for="public" class="block text-gray-700 font-medium text-left m-2">Openbaar</label>
                <input
                    type="checkbox"
                    id="public-toggle"
                    v-model="isPublic"
                    class="hidden"
                />
                <div class="bg-gray-300 w-12 h-6 rounded-full flex items-center p-1 transition duration-200 ease-in-out"
                     :class="{'bg-green-600': isPublic}">
                  <div class="bg-white w-4 h-4 rounded-full shadow-md transform transition duration-200 ease-in-out"
                       :class="{'translate-x-6': isPublic, 'translate-x-0': !isPublic}"></div>
                </div>
              </label>
            </div>
          </div>
        </div>

        <div v-if="error" class="text-red-500 mb-4">{{ error }}</div>

        <form ref="form" @submit="handleSubmit">
        <div class="flex gap-4 mt-4 mb-2">
          <!-- FORM NAME -->
          <div class="w-full md:w-1/2">
            <label for="form-name" class="block text-gray-700 font-medium text-left mb-2">Form naam</label>
            <input
                v-model="formName"
                type="text"
                name="form-name"
                class="w-full p-2 border rounded-lg text-gray-700 focus:outline-none focus:ring-2 focus:ring-blue-400"
                placeholder="Voer formnaam in"
                required
            >
          </div>

          <!-- CATEGORY -->
          <div class="w-full md:w-1/2">
            <label for="category" class="block text-gray-700 font-medium text-left mb-2">Categorie</label>
            <select v-model="selectedCategory" name="category" class="w-full px-4 py-2 border rounded-lg" required>
              <option value="" disabled selected>-- Selecteer een categorie --</option> <!-- Added a placeholder option -->
              <option v-for="category in categories" :key="category.id" :value="category.id">
                {{ category.name }}
              </option>
            </select>
          </div>
        </div>

          <div class="w-full">
            <label class="block text-gray-700 font-medium text-left mb-2">Nieuwe vraag</label>
            <div class="bg-gray-100 rounded-lg flex justify-between items-center text-gray-500 border">
              <input
                  v-model="newQuestion"
                  type="text"
                  placeholder="Voeg een nieuwe vraag toe"
                  class="w-full bg-gray-100 border-none m-2 text-gray-700 focus:outline-none focus:ring-0"
              />
              <div class="flex items-center mx-2">
                <label for="new-question-public" class="flex items-center cursor-pointer">
                  <!-- Tekst label -->
                  <span class="mr-2 text-sm font-medium text-gray-700">Openbaar</span>
                  <!-- Toggle switch -->
                  <div class="relative">
                    <input
                        id="new-question-public"
                        type="checkbox"
                        v-model="isNewQuestionPublic"
                        class="sr-only peer"
                    />
                    <div class="w-11 h-6 bg-gray-200 rounded-full peer peer-checked:bg-blue-600 peer-focus:ring-blue-300 transition"></div>
                    <div
                        class="absolute top-[2px] left-[2px] bg-white w-5 h-5 rounded-full shadow-md peer-checked:translate-x-full peer-checked:bg-white transition"></div>
                  </div>
                </label>
              </div>

              <button
                  @click="addCustomQuestion"
                  type="button"
                  class="ml-2 p-2 bg-transparent border-none cursor-pointer hover:text-blue-500"
                  :aria-label="'Voeg vraag toe'"
              >
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-6">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M12 9v6m3-3H9m12 0a9 9 0 1 1-18 0 9 9 0 0 1 18 0Z" />
                </svg>
              </button>
            </div>
          </div>

          <!-- //QUESTIONS -->
        <div class="flex flex-col gap-4 mt-2 md:flex-row">
          <div class="w-full md:w-1/2">
            <label class="block text-gray-700 font-medium text-left">Geselecteerde vragen</label>

            <ul class="h-72 overflow-y-auto flex-grow">
              <li v-for="question in selectedQuestions" :key="question.id" class="p-2 bg-gray-100 rounded-lg mt-2 mb-4 flex justify-between items-center border">
              <span>
                {{ question.question }}
              </span>
                <svg xmlns="http://www.w3.org/2000/svg"
                     viewBox="0 0 24 24"
                     fill="currentColor"
                     class="w-6 h-6 ml-2 cursor-pointer hover:text-red-500"
                     @click="removeQuestionFromList(question)"
                >
                  <path fill-rule="evenodd" d="M5.47 5.47a.75.75 0 0 1 1.06 0L12 10.94l5.47-5.47a.75.75 0 1 1 1.06 1.06L13.06 12l5.47 5.47a.75.75 0 1 1-1.06 1.06L12 13.06l-5.47 5.47a.75.75 0 0 1-1.06-1.06L10.94 12 5.47 6.53a.75.75 0 0 1 0-1.06Z" clip-rule="evenodd" />
                </svg>
              </li>
            </ul>
          </div>

          <div class="w-full md:w-1/2">
            <label class="block text-gray-700 font-medium text-left mb-2">Zoek vragen</label>
            <div v-if="error" class="text-red-500 mt-2">{{ error }}</div>
            <ul class="space-y-4 h-64 overflow-y-auto flex-grow">
              <input
                  v-model="searchTerm"
                  type="text"
                  name="searchTerm"
                  class="w-full px-4 py-2 pb-2 border rounded-lg text-gray-700 focus:outline-none"
                  placeholder="Zoeken..."

              >
              <li v-if="!loading && filteredQuestions.length === 0" class="text-gray-500">Geen vragen gevonden.</li>
              <li
                  v-for="question in filteredQuestions"
                  :key="question.id"
                  class="p-2 border border-gray-300 rounded-lg hover:bg-gray-50 cursor-pointer"
                  @click="addQuestionToList(question)"
              >
                <div class="flex justify-between items-center">
                <span>
                  {{ question.question }}
                </span>
                </div>
              </li>
            </ul>
          </div>
        </div>

        <!-- //SUBMIT BUTTON -->
        <div class="w-full md:w-auto flex justify-end">
          <button
              class="w-full px-6 py-2 mr-2 md:w-auto AXI-bg text-white rounded-lg AXI-bg-hover transition"
              :disabled="loading"
              @click="goBack()"
          >
            {{ 'Annuleren' }}
          </button>
          <button
              type="submit"
              class="w-full px-6 py-2 md:w-auto AXI-bg text-white rounded-lg AXI-bg-hover transition"
              :disabled="loading"
          >
            {{ loading ? 'Opslaan...' : 'Maken' }}
          </button>
        </div>
        </form>
      </div>
    </div>
  </div>
</template>