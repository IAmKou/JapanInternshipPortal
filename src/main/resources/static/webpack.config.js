const path = require('path');
const HtmlWebpackPlugin = require('html-webpack-plugin');

module.exports = {
    entry: './src/index.js', // Entry point for your JS
    output: {
        filename: 'bundle.js',
        path: path.resolve(__dirname, '../static/dist'), // Output the bundled files to Spring Boot's static directory
    },
    module: {
        rules: [
            {
                test: /\.css$/i,
                use: ['style-loader', 'css-loader'], // CSS handling
            },
        ],
    },
    plugins: [
        new HtmlWebpackPlugin({
            template: './src/index.html', // Create an HTML file based on your template
        }),
    ],
    devServer: {
        static: path.resolve(__dirname, '../static'),
        open: true, // Automatically open the browser
        port: 8080, // Change port if necessary
    },
};
