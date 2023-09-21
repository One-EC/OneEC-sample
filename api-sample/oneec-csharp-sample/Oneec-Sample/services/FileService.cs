using Newtonsoft.Json;
using Oneec_Sample.models;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;

namespace Oneec_Sample.services
{
    class FileService
    {
        internal void DownloadtoLocal(string rootPath, string data)
        {
            var shipDocument = JsonConvert.DeserializeObject<ShipDocumentModel>(data);
            var realPath = Path.Combine(rootPath, shipDocument.fileName);
            var dataByte = Convert.FromBase64String(shipDocument.data);
            File.WriteAllBytes(realPath, dataByte);

        }
    }
}
