import { defineConfig } from 'vite';

export default defineConfig({
  build: {
    // Put the built UI exactly where Spring Boot wants to serve static web files
    outDir: '../src/main/resources/static',
    emptyOutDir: true
  }
});
