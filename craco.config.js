const path = require('path');

module.exports = {
  webpack: {
    configure: (webpackConfig, { env, paths }) => {
      paths.appSrc = path.resolve(__dirname, 'src/main/webapp/src');
      paths.appPublic = path.resolve(__dirname, 'src/main/webapp/public');
      webpackConfig.entry = path.resolve(__dirname, 'src/main/webapp/src/index.tsx');

      // Ensure HTMLWebpackPlugin finds the correct template
      const htmlWebpackPlugin = webpackConfig.plugins.find(
        plugin => plugin.constructor.name === 'HtmlWebpackPlugin'
      );
      if (htmlWebpackPlugin) {
        htmlWebpackPlugin.options.template = path.resolve(__dirname, 'src/main/webapp/public/index.html');
      }

      return webpackConfig;
    },
  },
  devServer: {
    static: {
      directory: path.resolve(__dirname, 'src/main/webapp/public'),
      publicPath: '/',
    },
    // Ensure contentBase is set for older webpack-dev-server versions if needed
    // contentBase: path.resolve(__dirname, 'src/main/webapp/public'),
  },
};