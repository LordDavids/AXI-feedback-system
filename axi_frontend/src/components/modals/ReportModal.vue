<script setup lang="ts">
import {VueFinalModal} from "vue-final-modal";
import {computed, onMounted, ref} from "vue";
import {api} from "../../AxiosInstance.ts";

export type IconVariant = 'danger' | 'success' | 'none'

const categories = ref<Category[]>([]);
const selectedCategory = ref<number | null>(null);

const startDate = ref<string>();
const endDate = ref<string>();

const loading = ref(true);

const fetchCategories = async () => {
  await api.get(`/category/all`)
      .then((response) => {
        console.log(response.data);
        categories.value = response.data;
        console.log(categories.value)
      })
      .catch((err) => {

      })
      .finally(() => {
        loading.value = false;
      });
};

const props = defineProps<{
  title: string
  description: string
  icon: IconVariant,
  closeModal: () => void,
  teamId: string,
  userId?: string
}>()

const test = (teamId: string, userId?: string) => {
  return computed(() => {
    return `${import.meta.env.VITE_API_URL}/team/${teamId}/report${userId ? '/user/' + userId : '/all'}/pdf?startDate=${startDate.value}&endDate=${endDate.value}&categoryId=${selectedCategory.value}`
  })
};
onMounted(() => {
  fetchCategories();
});
</script>

<template>
  <VueFinalModal
      class="flex justify-center items-center"
      content-class="flex flex-col max-w-xl p-4 bg-white border rounded-xl space-y-2 text-center"
  >
    <div class="p-2">
      <i :class="'i-' + props.icon" class="rounded-xl p-7"/>
      <h1 class="text-4xl font-bold mb-2">
        {{ title }}
      </h1>
    </div>

    <!-- form for generating report -->

    <form class="flex flex-col space-y-4 py-2 px-4">
      <div class=" flex flex-row">
        <div class="flex flex-col text-left pr-8">
          <label class="block text-gray-700 font-medium text-left mb-2" for="start">Start datum:</label>
          <input v-model="startDate" class="border text-gray-700 p-1 rounded" type="date" id="start" name="start" required>
        </div>
        <div class="flex flex-col text-left">
          <label class="block text-gray-700 font-medium text-left mb-2" for="end">Eind datum:</label>
          <input v-model="endDate" class="border text-gray-700 p-1 rounded" type="date" id="end" name="end" required>
        </div>
      </div>

      <div class="w-full ">
        <label for="category" class="block text-gray-700 font-medium text-left mb-2">Categorie</label>
        <select v-model="selectedCategory" name="category" class="w-full px-4 py-2 border rounded-lg " required>
          <option value="" disabled selected>-- Selecteer een categorie --</option> <!-- Added a placeholder option -->
          <option v-if="!loading" v-for="category in categories" :key="category.id" :value="category.id">
            {{ category.name }}
          </option>
        </select>
      </div>
      <a v-if="startDate && endDate && selectedCategory" target="_blank" class="btn primary !mt-8" :href="test(teamId, userId).value" type="submit">Genereer</a>
      <span v-else class="block btn primary !mt-8 !bg-gray-300 !text-gray-500 !cursor-auto">Genereer</span>
      <button class="btn secondary !mt-3" @click="closeModal" type="button">Annuleren</button>
    </form>

  </VueFinalModal>
</template>

<style scoped>
.primary {
  background-color: #7B42FA;
  color: white;
}

.primary:hover {
  background-color: #733dea;
}

.secondary {
  @apply text-black bg-transparent border border-gray-300;
}

.secondary:hover {
  @apply bg-gray-50;
}

.danger {
  background-color: #e3342f;
  color: white;
}

.danger:hover {
  background-color: #d23934;
}

.success {
  background-color: #38c172;
  color: white;
}

.success:hover {
  background-color: #2f855a;
}

i {
  width: 24px;
  height: 24px;
  background-size: contain;
  background-repeat: no-repeat;
  background-position: center;
  display: inline-block;
}

.i-danger {
  background-color: #FEE2E2;
  background-image: url('data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="-3 -3 30 30" stroke-width="1.5" stroke="%23EF4444" %3E%3Cpath stroke-linecap="round" stroke-linejoin="round" d="M12 9v3.75m-9.303 3.376c-.866 1.5.217 3.374 1.948 3.374h14.71c1.73 0 2.813-1.874 1.948-3.374L13.949 3.378c-.866-1.5-3.032-1.5-3.898 0L2.697 16.126ZM12 15.75h.007v.008H12v-.008Z"%3E%3C/path%3E%3C/svg%3E');
}

.i-success {
  background-color: #DCFCE7;
  background-image: url('data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="-3 -3 30 30" stroke-width="1.5" stroke="%2322C55E" %3E%3Cpath stroke-linecap="round" stroke-linejoin="round" d="M9 12.75 11.25 15 15 9.75M21 12a9 9 0 1 1-18 0 9 9 0 0 1 18 0Z"%3E%3C/path%3E%3C/svg%3E');
}

.i-none {
  display: none;
}
</style>