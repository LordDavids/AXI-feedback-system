<script setup lang="ts" generic="T">
import { VueFinalModal } from 'vue-final-modal'
import Multiselect from 'vue-multiselect'
import {ref} from "vue";

const props = defineProps<{
  title: string
  fieldName: string
  options: T[]
  target: User
}>()

export type MultiselectSubmitResult = {
  target: User,
  selected: T[]
}

const emit = defineEmits<{
  (e: 'submit'): MultiselectSubmitResult
  (e: 'cancel'): void
}>()

const selected = ref(
    props.options.filter(role => props.target.roles.includes(role.roleName))
)

const submit = () => {
  emit('submit', {
    target: props.target,
    selected: selected.value
  })
}
</script>

<template>
  <VueFinalModal
      class="flex justify-center items-center"
      content-class="flex flex-col max-w-xl mx-4 p-4 bg-white border rounded-lg space-y-2 w-1/2"
  >
    <h1 class="text-xl font-normal">
      {{ title }}
      <span>
         {{ target.firstName }} {{ target.infix ? target.infix + ' ' : '' }} {{ target.lastName }}
      </span>
    </h1>

    <label for="roles" class="block text-sm font-medium text-gray-700 text-left">{{ fieldName }}:</label>
    <div class="form-group mt-4">
      <multiselect class="mt-1 block w-full shadow-sm mb-4" v-model="selected" :options="options" :multiple="true" :close-on-select="false" :clear-on-select="false" :preserve-search="true" placeholder="Selecteer rol" label="roleName" track-by="id" id="roles"></multiselect>
    </div>
    
    <button class="btn btn-green !mt-auto" @click="submit">
      Aanpassen
    </button>
  </VueFinalModal>
</template>

<style src="vue-multiselect/dist/vue-multiselect.min.css"></style>

<style scoped>
</style>