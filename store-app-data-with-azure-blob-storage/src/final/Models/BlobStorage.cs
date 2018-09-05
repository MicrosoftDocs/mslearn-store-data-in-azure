using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.Extensions.Options;
using Microsoft.WindowsAzure.Storage;
using Microsoft.WindowsAzure.Storage.Blob;

namespace FileUploader.Models
{
    public class BlobStorage : IStorage
    {
        private readonly AzureStorageConfig storageConfig;

        public BlobStorage(IOptions<AzureStorageConfig> storageConfig)
        {
            this.storageConfig = storageConfig.Value;
        }

        public Task Initialize()
        {
            CloudStorageAccount storageAccount = CloudStorageAccount.Parse(storageConfig.ConnectionString);
            CloudBlobClient blobClient = storageAccount.CreateCloudBlobClient();
            CloudBlobContainer container = blobClient.GetContainerReference(storageConfig.FileContainerName);
            return container.CreateIfNotExistsAsync();
        }

        public Task Save(Stream fileStream, string name)
        {
            CloudStorageAccount storageAccount = CloudStorageAccount.Parse(storageConfig.ConnectionString);
            CloudBlobClient blobClient = storageAccount.CreateCloudBlobClient();
            CloudBlobContainer container = blobClient.GetContainerReference(storageConfig.FileContainerName);            
            CloudBlockBlob blockBlob = container.GetBlockBlobReference(name);
            return blockBlob.UploadFromStreamAsync(fileStream);
        }

        public async Task<IEnumerable<string>> GetNames()
        {
            List<string> names = new List<string>();

            CloudStorageAccount storageAccount = CloudStorageAccount.Parse(storageConfig.ConnectionString);
            CloudBlobClient blobClient = storageAccount.CreateCloudBlobClient();
            CloudBlobContainer container = blobClient.GetContainerReference(storageConfig.FileContainerName);

            BlobContinuationToken continuationToken = null;
            BlobResultSegment resultSegment = null;

            do
            {
                resultSegment = await container.ListBlobsSegmentedAsync(continuationToken);

                // Get the name of each blob.
                names.AddRange(resultSegment.Results.OfType<CloudBlockBlob>().Select(b => b.Name));

                continuationToken = resultSegment.ContinuationToken;
            } while (continuationToken != null);

            return names;
        }

        public Task<Stream> Load(string name)
        {
            CloudStorageAccount storageAccount = CloudStorageAccount.Parse(storageConfig.ConnectionString);
            CloudBlobClient blobClient = storageAccount.CreateCloudBlobClient();
            CloudBlobContainer container = blobClient.GetContainerReference(storageConfig.FileContainerName);
            CloudBlockBlob blockBlob = container.GetBlockBlobReference(name);
            return blockBlob.OpenReadAsync();
        }
    }
}