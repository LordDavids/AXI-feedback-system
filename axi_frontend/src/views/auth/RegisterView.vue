  <script setup lang="ts">
  import {computed, onMounted, ref} from 'vue';
  import { useRoute, useRouter } from 'vue-router';
  import { api } from "../../AxiosInstance.ts";
  import Multiselect from 'vue-multiselect'


  const firstName = ref('');
  const infix = ref('');
  const lastName = ref('');
  const email = ref('');
  const password = ref('');
  const confirmPassword = ref('');
  const Roles = ref<Role[]>([]);
  const value = ref<Role[]>([]);


  const errorMessage = ref('');
  const loading = ref(false);
  const router = useRouter();


  const doPasswordsMatch = computed(() => {
    if (confirmPassword.value.length === 0) return '';
    return password.value === confirmPassword.value ? 'Wachtwoorden komen overeen' : 'Wachtwoorden komen niet overeen';
  });

  const passwordValidationMessage = computed(() => {
    if (password.value.length === 0) return '';

    return isValidPassword(password.value)
        ? 'Sterk wachtwoord'
        : 'Het wachtwoord moet minstens 5 karakters, 1 hoofdletter, 1 nummer, en 1 speciaal karakter bevatten.';
  });


  const isValidPassword = (password: string) => {
    const regex = /^(?=.*[A-Z])(?=.*\d)(?=.*\W).{5,}$/;
    return regex.test(password);
  };


  const handleSubmit = (event: Event) => {
    event.preventDefault();
    errorMessage.value = '';

    if (value.value.length === 0) {
      errorMessage.value = 'Selecteer een rol.';
      return;
    }

    if (!isValidPassword(password.value)) {
      errorMessage.value = 'Wachtwoord voldoet niet aan de eisen.';
      return;
    }

    if (password.value !== confirmPassword.value) {
      errorMessage.value = 'Wachtwoorden komen niet overeen.';
      return;
    }

    loading.value = true;

    api.post(import.meta.env.VITE_API_URL + "/auth/register", {
      firstName: firstName.value,
      infix: infix.value,
      lastName: lastName.value,
      email: email.value,
      password: password.value,
      roles: value.value.map((role) => role.id)
    }, {
      headers: {
        'Access-Control-Allow-Origin': '*',
        'Content-Type': 'application/json'
      }
    })
        .then((response) => {
          router.push("/");
        })
        .catch((error) => {
          console.log(error)
          if (error?.response?.status === 409) {
            errorMessage.value = "Gebruiker met dit email bestaat al.";
          } else if (error?.response?.status === 500) {
            errorMessage.value = "Er is een fout opgetreden bij de server.";
          } else if (error?.response?.status === 400) {
            errorMessage.value = "Vul alle velden in.";
          } else {
            errorMessage.value = "Er is een onbekende fout opgetreden. Probeer het later nog een keer.";
          }
        })
        .finally(() => {
          loading.value = false;
        });
  };

  onMounted(() => {
    api.get<Role[]>(import.meta.env.VITE_API_URL + "/roles")
        .then((response) => {
          Roles.value = response.data;
        })
        .catch((error) => {
          if (error?.response?.status === 500) {
            errorMessage.value = "Er is een fout opgetreden bij de server.";
          } else {
            errorMessage.value = "Er is een onbekende fout opgetreden. Probeer het later nog een keer.";
          }
        });

  })
  </script>



  <template>
    <div>
      <div class="flex flex-col items-center justify-center mt-8">
        <div class="w-full max-w-sm bg-white p-6 rounded-lg shadow-lg">
          <h1 class="text-3xl font-bold mb-5 text-center">Registreren</h1>
          <form @submit.prevent="handleSubmit" class="flex flex-col">
            <div class="flex flex-row">
              <div class="form-group me-1.5">
                <label for="firstName" class="block text-sm font-medium text-gray-700 text-left">Voornaam:</label>
                <input type="text" id="firstName" v-model="firstName" required class="mt-1 block w-full p-2 border border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm">
              </div>
              <div class="form-group">
                <label for="infix" class="block text-sm font-medium text-gray-700 text-left">Tussenvoegsel:</label>
                <input type="text" id="infix" v-model="infix" class="mt-1 w-full block p-2 border border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm">
              </div>
            </div>
            <div class="form-group mt-1.5">
              <label for="lastName" class="block text-sm font-medium text-gray-700 text-left">Achternaam:</label>
              <input type="text" id="lastName" v-model="lastName" required class="mt-1 block w-full p-2 border border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm">
            </div>
            <div class="form-group mt-4">
              <label for="roles" class="block text-sm font-medium text-gray-700 text-left">Rol:</label>
              <multiselect class="mt-1 block w-full shadow-sm " v-model="value" :options="Roles" :multiple="true" :close-on-select="false" :clear-on-select="false" :preserve-search="true" placeholder="Selecteer rol" label="roleName" track-by="id" id="roles"></multiselect>
            </div>
            <div class="form-group mt-4">
              <label for="email" class="block text-sm font-medium text-gray-700 text-left">Email:</label>
              <input type="email" id="email" v-model="email" required class="mt-1 block w-full p-2 border border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm">
            </div>
            <div class="form-group mt-4">
              <label for="password" class="block text-sm font-medium text-gray-700 text-left">Wachtwoord:</label>
              <input type="password" id="password" v-model="password" required class="mt-1 block w-full p-2 border border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm">
              <p class="text-sm text-gray-500">{{ passwordValidationMessage }}</p>
            </div>
            <div class="form-group mt-4">
              <label for="confirmPassword" class="block text-sm font-medium text-gray-700 text-left">Wachtwoord bevestigen:</label>
              <input type="password" id="confirmPassword" v-model="confirmPassword" required class="mt-1 block w-full p-2 border border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm">
              <p class="text-sm text-gray-500">{{ doPasswordsMatch }}</p>
            </div>
            <button type="submit" class="mt-4 w-full AXI-bg AXI-bg-hover  text-white py-2 px-4 rounded-md  focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500" :disabled="loading">
              Registreren
            </button>
            <p v-if="errorMessage" id="error-message" class="text-red-500 text-sm mt-6">{{ errorMessage }}</p>
          </form>
        </div>
      </div>
    </div>
  </template>

  <style src="vue-multiselect/dist/vue-multiselect.min.css"></style>
  <style scoped>

  </style>