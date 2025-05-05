module.exports = {
  devServer: {
    proxy: {
      '/business': {
        target: 'http://localhost:8000',
        changeOrigin: true,
        pathRewrite: { '^/business': '' }
      }
    },
    client: {
      overlay: false
    }
  }
};
