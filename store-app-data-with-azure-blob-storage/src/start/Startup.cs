using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Hosting;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using FileUploader.Models;
using Microsoft.Extensions.Options;

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

            services.AddMvc();
        }

        public void Configure(IApplicationBuilder app, IHostingEnvironment env)
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

            app.UseMvc(routes =>
            {
                routes.MapRoute(
                    name: "default",
                    template: "{controller=Home}/{action=Index}/{id?}");
            });
        }
    }
}
