import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

public class GitServiceImpl {

	Repository repository;
	RevCommit currentCommit;
	RevWalk walk;

	public Repository getCloneEmail(String projectPath) throws Exception {

		File folder = new File(projectPath);
		Git git;
		if (folder.exists()) {
			RepositoryBuilder builder = new RepositoryBuilder();
			Repository repository = builder.setGitDir(new File(folder, ".git")).readEnvironment().findGitDir().build();
			git = new Git(repository);

		} else {
			System.err.println("Repositorio nao clonado: " + projectPath);
			return null;
		}
		return git.getRepository();
	}

	public void DownloadRepo(File folder2, String cloneUrl) {

		// se folder 2 já existe na pasta , entao atribui algo no nome da pasta
		// para trocar o arquivo.
		File newDirect = verifyDirectory(folder2);
		try {
			Git.cloneRepository().setDirectory(newDirect).setURI(cloneUrl).setCloneAllBranches(false).call();
		} catch (Exception e) {
			System.out.println("Aqui " + e);
			System.err.println(e);
		}

	}

	private File verifyDirectory(File folder2) {

		File file = new File("C:\\Temp\\gitRepositories");
		File diretorio = file;
		File[] arquivos = diretorio.listFiles();
		File newFolder = folder2;// = new File(folder2+"f");

		Random r = new Random();
		int num = 1 + r.nextInt(300);

		if (arquivos != null) {
			int length = arquivos.length;
			for (int i = 0; i < length; ++i) {
				File f = arquivos[i];
				if (f.isDirectory() && f.getName() == folder2.getName() || f.getName().contains(folder2.getName())) {
					System.out.println("O diretorio já existe: " + f.getAbsolutePath() + "Novo  ddiretorio: " + folder2
							+ Integer.toString(num));
					newFolder = new File(folder2 + Integer.toString(num));
				}
			}
		}
		return newFolder;
	}

	public void searchEmail(File directory)
			throws MissingObjectException, IncorrectObjectTypeException, AmbiguousObjectException, IOException {

		FileWriter writer = new FileWriter(directory+"\\email.csv");
		// cabeçalho file csv
		writer.append("NameProject");
		writer.append(';');
		writer.append("Size MB");
		writer.append(';');
		writer.append("Author");
		writer.append(';');
		writer.append("E-mail");
		writer.append('\n');

		//File diretorio = new File(directory);
		File[] arquivos = directory.listFiles();

		if (arquivos != null) {
			int length = arquivos.length;
			for (int i = 0; i < length; ++i) {
				File f = arquivos[i];
				if (f.isDirectory()) {
					System.out.println("Directory: " + f.getAbsolutePath());
					try {
						repository = getCloneEmail(directory+"\\"+ f.getName());
					} catch (Exception e) {
						e.printStackTrace();
					}
					currentCommit = null;
					RevWalk walk = new RevWalk(repository);
					walk.markStart(walk.parseCommit(repository.resolve("HEAD")));
					Iterator<RevCommit> x = walk.iterator();
					int count = 0;

					while (x.hasNext()) {
						currentCommit = x.next();

						long size = FileUtils.sizeOfDirectory(new File(f.getAbsolutePath()));
						System.out.println("Size: " + size / (1024 * 1024) + " MB");
						size = size / (1024 * 1024);

						writer.append(f.getName());
						writer.append(';');
						writer.append(Long.toString(size));
						writer.append(';');
						writer.append(currentCommit.getAuthorIdent().getName());
						writer.append(';');
						writer.append(currentCommit.getAuthorIdent().getEmailAddress());
						writer.append('\n');
						System.out.println(currentCommit.getAuthorIdent().getName()
								+ currentCommit.getAuthorIdent().getEmailAddress());
						// break;
						if (count == 3) {
							break;
						}
						count++;
					}
				}
			}
			writer.flush();
			writer.close();
		}
	}
}