<script setup lang="ts">
import {computed, onMounted, ref, watch} from 'vue';
import { api } from "../AxiosInstance.ts";
import { useRouter } from 'vue-router';
import { useRoute } from 'vue-router';
import {useModal} from "vue-final-modal";
import ModalConfirm from "../components/modals/MultiselectModal.vue";
import Breadcrumbs from '../components/Breadcrumbs.vue';
import ConfirmModal from "../components/modals/ConfirmModal.vue";

// Reactive state
const teamName = ref<string>('');
const loading = ref(false);
const error = ref<string | null>(null);
const searchTerm = ref<string>('');
const users = ref<User[]>([]);
const selectedUsers = ref<User[]>([]);
const team = ref<Team>(null);

const router = useRouter();
const route = useRoute();


const editTeam = async () => {
  loading.value = true;
  error.value = null;

  const teamId = route.params.id;

  await api.post(`/team/${teamId}/edit`, {
    teamName: teamName.value,
    users: selectedUsers.value.map(user => user.id)
  })
      .then((response) => {
        router.push("/team/" + teamId);
      })
      .catch((err) => {
        error.value = err.message;
      })
      .finally(() => {
        loading.value = false;
      });
};

const filteredUsers = computed(() => {
  return users.value.filter(user =>
      !selectedUsers.value.some(selectedUser => selectedUser.id === user.id)
  );
});

// Handle form submission
const handleSubmit = (e: Event) => {
  e.preventDefault(); // Prevent default form submission
  editTeam(); // Call the createTeam method
};

let debounceTimeout: ReturnType<typeof setTimeout>;

const searchUsers = async () => {
  error.value = null;

  await api.get<User[]>('/user/search', {
    params: {
      searchTerm: searchTerm.value.trim(),
    }
  })
      .then((response) => {
        users.value = response.data;
      })
      .catch((err) => {
        error.value = err.message;
      })
};



const removeUserFromList = (user: User) => {
  const confirmDeletion = (user: User) => {
    const index = selectedUsers.value.findIndex(selectedUser => selectedUser.id === user.id);
    if (index !== -1) {
      selectedUsers.value.splice(index, 1);
    }
  }
  
  let closeModal = () => {};
  
  const { open, close } = useModal({
    component: ConfirmModal,
    attrs: {
      title: 'Weet je het zeker?',
      description: `Weet je zeker dat je ${user.firstName} ${user.infix ? user.infix : ''} ${user.lastName} uit het team wilt verwijderen?`,
      icon: 'danger',
      buttons: [
        {
          text: 'Verwijderen',
          variant: 'danger',
          action: () => {
            confirmDeletion(user);
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


const fetchTeamDetails = async () => {
  loading.value = true;
  error.value = null;

  const teamId = route.params.id;

  await api.get(`/team/${teamId}`)
      .then((response) => {
        team.value = response.data;
        teamName.value = team.value.name;
        selectedUsers.value = team.value.users;
      })
      .catch((err) => {
        error.value = err.message;
      })
      .finally(() => {
        loading.value = false;
      });
};

const debouncedSearchUsers = () => {
  clearTimeout(debounceTimeout);
  debounceTimeout = setTimeout(() => {
    searchUsers();
  }, 200);
};

const addUserToList = (user: User) => {
  if (!selectedUsers.value.some(selectedUser => selectedUser.id === user.id)) {
    selectedUsers.value.push(user);
  }
};

watch(searchTerm, debouncedSearchUsers);

onMounted(
  searchUsers(),
  fetchTeamDetails(),
);

</script>

<template>
  <div class="container px-0 sm:px-4">
    <div class="flex flex-col gap-2 p-6 rounded-lg shadow-lg bg-white">
      <Breadcrumbs />
      <h1 class="hidden md:hidden lg:block text-2xl font-bold mb-4 text-center">Edit Team</h1>
      <div v-if="error" class="text-red-500 mb-4">{{ error }}</div>
      <form @submit="handleSubmit" class="flex flex-col md:flex-row items-end w-full gap-4 mb-4">
        <div class="w-full">
          <label for="team-name" class="block text-gray-700 font-medium text-left mb-2">Team naam</label>
          <input
              v-model="teamName"
              type="text"
              name="team-name"
              class="w-full px-4 py-2 border rounded-lg text-gray-700 focus:outline-none focus:ring-2 focus:ring-blue-400"
              placeholder="Voer teamnaam in"
              required
          >
        </div>
        <div class="w-full md:w-auto">
          <button
              type="submit"
              class="w-full px-6 py-2 md:w-auto AXI-bg text-white rounded-lg AXI-bg-hover transition"
              :disabled="loading"
          >
            {{ loading ? 'Opslaan...' : 'Aanpassen' }}
          </button>
        </div>
      </form>

      <div class="flex flex-col md:flex-row gap-4">
        <div class="flex flex-col w-full md:w-1/2">
          <label class="block text-gray-700 font-medium text-left">Geselecteerde gebruikers</label>
          <ul class="min-h-80 max-h-80 overflow-y-auto flex-grow">
            <li v-for="user in selectedUsers" :key="user.id" class="p-4 mt-4 mb-4 bg-gray-100 border border-white rounded-lg flex justify-between items-center">
              <span>
                {{ user.firstName }} {{ user.infix ? user.infix + ' ' : '' }}{{ user.lastName }}
              </span>
              <svg xmlns="http://www.w3.org/2000/svg"
                   viewBox="0 0 24 24"
                   fill="currentColor"
                   class="w-6 h-6 ml-2 cursor-pointer hover:text-red-500"
                   @click="removeUserFromList(user)"
              >
                <path fill-rule="evenodd" d="M5.47 5.47a.75.75 0 0 1 1.06 0L12 10.94l5.47-5.47a.75.75 0 1 1 1.06 1.06L13.06 12l5.47 5.47a.75.75 0 1 1-1.06 1.06L12 13.06l-5.47 5.47a.75.75 0 0 1-1.06-1.06L10.94 12 5.47 6.53a.75.75 0 0 1 0-1.06Z" clip-rule="evenodd" />
              </svg>
            </li>
          </ul>
        </div>

        <div class="flex flex-col w-full md:w-1/2">
          <label class="block text-gray-700 font-medium text-left">Zoek gebruikers</label>
          <div v-if="error" class="text-red-500 mt-2">{{ error }}</div>
          <input
              v-model="searchTerm"
              type="text"
              name="searchTerm"
              class="w-full px-4 py-2 pb-2 border rounded-lg text-gray-700 focus:outline-none focus:ring-2 focus:ring-blue-400 mt-4 mb-4"
              placeholder="Zoeken..."
              required
          >
          <ul class="min-h-72 max-h-72 overflow-y-auto flex-grow">
            <li v-if="!loading && filteredUsers.length === 0" class="text-gray-500">Geen gebruikers gevonden.</li>
            <li
                v-for="user in filteredUsers"
                :key="user.id"
                class="p-4 mt-4 mb-4 border border-gray-300 rounded-lg hover:bg-gray-50 cursor-pointer"
                @click="addUserToList(user)"
            >
              <div class="flex justify-between items-center">
                <span>
                  {{ user.firstName }} {{ user.infix ? user.infix + ' ' : '' }}{{ user.lastName }}
                </span>
                <span v-if="user.roles.includes('manager')" class="text-gray-500">Manager</span>
              </div>
            </li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>