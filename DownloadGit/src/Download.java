import java.io.File;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

import com.jcabi.github.Github;
import com.jcabi.github.RtGithub;
import com.jcabi.http.Request;
import com.jcabi.http.response.JsonResponse;

public class Download {
	public static void main(String[] args) throws Exception {

		GitServiceImpl s = new GitServiceImpl();

		String baseFolder = "C:\\temp\\e-commerce";
		
		Github github = new RtGithub("communications227@gmail.com", "_a1234567890");

		Request request;
		JsonArray items;
		int cont = 1;
		int numRepository = 0;
		int page = 1;

		request = github.entry().uri().path("/search/repositories")
				.queryParam("q", "language:java e-commerce size:>1000")
				.queryParam("sort", "stars")
				.queryParam("per_page", "100")
				.queryParam("page", String.valueOf(page))
				.back().method(Request.GET);

		System.out.println("String " + request.uri().toString());

		items = request.fetch().as(JsonResponse.class).json().readObject().getJsonArray("items");
		
		System.out.println(request.fetch().as(JsonResponse.class).json().readObject().getInt("total_count"));
		
		int totalProjectsGit = request.fetch().as(JsonResponse.class).json().readObject().getInt("total_count");

		System.out.println("Total pages " + totalProjectsGit / 100);
		
		int tot = totalProjectsGit / 100;
		int total=0;
		while (total<=page) {
			page++;
			for (JsonValue item : items) {
			
				JsonObject repoData = (JsonObject) item;
				String line = repoData.getString("name") + "," 
						+ repoData.getString("git_url") + ","
						+ repoData.getInt("stargazers_count") + "," // stars
						+ repoData.getInt("forks_count") + "," // forks
						+ repoData.getInt("size"); // tags

				File folder = new File(baseFolder + "/" + repoData.getString("name") + repoData.getInt("id"));
				try {
					s.DownloadRepo(folder, repoData.getString("git_url"));
				} catch (Exception ex) {
					System.out.println(ex);
				}
				System.out.println(cont + "" + "\t" + line);
				//System.out.println("String " + request.uri().toString());
				
				
				cont++;
			}
			//page++;

			request = github.entry().uri().path("/search/repositories")
					.queryParam("q", "language:Java accounting size:>1000")
					.queryParam("sort", "stars")
					.queryParam("per_page", "100")
					.queryParam("page", String.valueOf(page))
					.back().method(Request.GET);
			
			items = request.fetch().as(JsonResponse.class).json().readObject().getJsonArray("items");
			

			System.out.println("String " + request.uri().toString());
			System.out.println("Total projects Git " + tot + "  " + " Baixados " + cont);
			System.out.println("Pages "+page);
			total++;

		}
	}
}