import type { Theme } from 'vitepress'
import DefaultTheme from 'vitepress/theme'
import ChapterList from './ChapterList.vue'

export default {
  extends: DefaultTheme,
  enhanceApp({ app }) {
    app.component('ChapterList', ChapterList)
  },
} satisfies Theme
