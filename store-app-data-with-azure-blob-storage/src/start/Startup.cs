using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Hosting;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using FileUploader.Models;
using Microsoft.Extensions.Options;
using Microsoft.Extensions.Hosting;

namespace FileUploader
{
    public class Startup
    {
        public Startup(IConfiguration configuration)
        {
            Configuration = configuration;
        }

        public IConfiguration Configuration { get; }

        public void ConfigureServices(IServiceCollection services)
        {
            // Set up IOptions and populate AzureStorageConfig from configuration
            services.AddOptions();
            services.Configure<AzureStorageConfig>(Configuration.GetSection("AzureStorageConfig"));

            // Wire up a single instance of BlobStorage, calling Initialize() when we first use it.
            services.AddSingleton<IStorage>(serviceProvider => {
                var blobStorage = new BlobStorage(serviceProvider.GetService<IOptions<AzureStorageConfig>>());
                blobStorage.Initialize().GetAwaiter().GetResult();
                return blobStorage;
            });

            services.AddControllersWithViews();
            services.AddRazorPages();
        }

        public void Configure(IApplicationBuilder app, IWebHostEnvironment env)
        {
            if (env.IsDevelopment())
            {
                app.UseDeveloperExceptionPage();
            }
            else
            {
                app.UseExceptionHandler("/Home/Error");
            }

            app.UseStaticFiles();

            app.UseRouting();

            app.UseEndpoints(routes =>
            {
                routes.MapDefaultControllerRoute();
                routes.MapControllers();
            });
        }
    }
}
